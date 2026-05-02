package com.custard.ehr.pharmacy.application.services;

import com.custard.ehr.pharmacy.application.dto.DispenseItemRequest;
import com.custard.ehr.pharmacy.application.dto.DispensePrescriptionRequest;
import com.custard.ehr.pharmacy.application.dto.DispenseRecordResponse;
import com.custard.ehr.pharmacy.application.ports.DispenseRepository;
import com.custard.ehr.pharmacy.application.ports.StockMovementRepository;
import com.custard.ehr.pharmacy.application.ports.StockRepository;
import com.custard.ehr.pharmacy.domain.DispenseRecord;
import com.custard.ehr.pharmacy.domain.StockMovement;
import com.custard.ehr.pharmacy.domain.StockMovementType;

import com.custard.ehr.prescription.PrescriptionIdentifierVerifier;
import com.custard.ehr.prescription.PrescriptionItemLookupView;
import com.custard.ehr.prescription.PrescriptionLookupView;
import com.custard.ehr.shared.events.PrescriptionDispensedEvent;
import com.custard.ehr.shared.events.PrescriptionItemNotDispensedEvent;
import com.custard.ehr.shared.exception.BusinessException;
import com.custard.ehr.shared.exception.NotFoundException;
import com.custard.ehr.shared.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DispensingService {

    private final Logger log = LoggerFactory.getLogger(DispensingService.class);

    private final DispenseRepository dispenseRepository;
    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;
    private final PrescriptionIdentifierVerifier prescriptionIdentifierVerifier;
    private final ApplicationEventPublisher eventPublisher;

    public DispensingService(
            DispenseRepository dispenseRepository,
            StockRepository stockRepository,
            StockMovementRepository stockMovementRepository,
            PrescriptionIdentifierVerifier prescriptionIdentifierVerifier,
            ApplicationEventPublisher eventPublisher
    ) {
        this.dispenseRepository = dispenseRepository;
        this.stockRepository = stockRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.prescriptionIdentifierVerifier = prescriptionIdentifierVerifier;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public DispenseRecordResponse dispense(DispensePrescriptionRequest request) {
        log.info("Starting selective dispensing for prescription {}", request.prescriptionId());

        if (dispenseRepository.existsByPrescriptionId(request.prescriptionId())) {
            log.warn("Dispensing blocked. Prescription {} has already been processed", request.prescriptionId());
            throw new BusinessException("Prescription has already been dispensed or partially dispensed");
        }

        PrescriptionLookupView prescription = prescriptionIdentifierVerifier
                .findDispensablePrescription(request.prescriptionId())
                .orElseThrow(() -> {
                    log.warn("Prescription {} is not dispensable", request.prescriptionId());
                    return new BusinessException("Prescription is not ready for dispensing");
                });

        UUID pharmacistId = SecurityUtils.requireCurrentUserId();

        Map<UUID, PrescriptionItemLookupView> prescribedItems = prescription.items()
                .stream()
                .collect(Collectors.toMap(
                        PrescriptionItemLookupView::prescriptionItemId,
                        Function.identity()
                ));

        validateRequestItems(request, prescribedItems);

        DispenseRecord record = new DispenseRecord(
                prescription.prescriptionId(),
                prescription.encounterId(),
                prescription.patientId(),
                pharmacistId
        );

        for (DispenseItemRequest itemRequest : request.items()) {
            PrescriptionItemLookupView prescribedItem = prescribedItems.get(itemRequest.prescriptionItemId());

            int prescribedQuantity = normalizePrescribedQuantity(prescribedItem.prescribedQuantity());
            int quantityToDispense = itemRequest.quantityToDispense();

            log.debug(
                    "Processing prescription item {} drug {}. Prescribed={}, dispensing={}",
                    prescribedItem.prescriptionItemId(),
                    prescribedItem.drugName(),
                    prescribedQuantity,
                    quantityToDispense
            );

            if (quantityToDispense > 0) {
                deductStock(
                        prescribedItem,
                        quantityToDispense,
                        record.getId(),
                        pharmacistId
                );
            }

            record.addItem(
                    prescribedItem.prescriptionItemId(),
                    prescribedItem.drugId(),
                    prescribedItem.drugName(),
                    prescribedQuantity,
                    quantityToDispense,
                    itemRequest.reason(),
                    itemRequest.note()
            );
        }

        record.validateHasItems();

        DispenseRecord saved = dispenseRepository.save(record);

        if (saved.isPartial()) {
            prescriptionIdentifierVerifier.markPartiallyDispensed(prescription.prescriptionId());
        } else {
            prescriptionIdentifierVerifier.markFullyDispensed(prescription.prescriptionId());
        }

        publishEvents(saved);

        log.info(
                "Selective dispensing completed. DispenseRecord={}, Prescription={}, Partial={}",
                saved.getId(),
                saved.getPrescriptionId(),
                saved.isPartial()
        );

        return DispenseRecordResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public DispenseRecordResponse getById(UUID dispenseRecordId) {
        log.debug("Fetching dispense record {}", dispenseRecordId);

        return dispenseRepository.findById(dispenseRecordId)
                .map(DispenseRecordResponse::from)
                .orElseThrow(() -> {
                    log.warn("Dispense record {} not found", dispenseRecordId);
                    return new NotFoundException("Dispense record not found");
                });
    }

    @Transactional(readOnly = true)
    public List<DispenseRecordResponse> getByPatient(UUID patientId) {
        log.debug("Fetching dispense records for patient {}", patientId);

        return dispenseRepository.findByPatientIdOrderByDispensedAtDesc(patientId)
                .stream()
                .map(DispenseRecordResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DispenseRecordResponse> getByPrescription(UUID prescriptionId) {
        log.debug("Fetching dispense records for prescription {}", prescriptionId);

        return dispenseRepository.findByPrescriptionIdOrderByDispensedAtDesc(prescriptionId)
                .stream()
                .map(DispenseRecordResponse::from)
                .toList();
    }

    private void validateRequestItems(
            DispensePrescriptionRequest request,
            Map<UUID, PrescriptionItemLookupView> prescribedItems
    ) {
        Set<UUID> seen = new HashSet<>();

        for (DispenseItemRequest item : request.items()) {
            if (!seen.add(item.prescriptionItemId())) {
                throw new BusinessException("Duplicate prescription item in dispense request");
            }

            if (!prescribedItems.containsKey(item.prescriptionItemId())) {
                throw new BusinessException("Dispense request contains item not found in prescription");
            }

            if (item.quantityToDispense() == null || item.quantityToDispense() < 0) {
                throw new BusinessException("Quantity to dispense cannot be negative");
            }

            PrescriptionItemLookupView prescribedItem = prescribedItems.get(item.prescriptionItemId());
            int prescribedQuantity = normalizePrescribedQuantity(prescribedItem.prescribedQuantity());

            if (item.quantityToDispense() > prescribedQuantity) {
                throw new BusinessException("Cannot dispense more than prescribed quantity");
            }

            if (item.quantityToDispense() < prescribedQuantity && item.reason() == null) {
                throw new BusinessException("Reason is required for partially dispensed or non-dispensed item");
            }
        }
    }

    private void deductStock(
            PrescriptionItemLookupView prescribedItem,
            int quantityToDispense,
            UUID dispenseRecordId,
            UUID pharmacistId
    ) {
        var stock = stockRepository.findByDrugId(prescribedItem.drugId())
                .orElseThrow(() -> {
                    log.warn("Stock not found for drug {}", prescribedItem.drugId());
                    return new BusinessException("Stock not found for drug: " + prescribedItem.drugName());
                });

        stock.deduct(quantityToDispense);
        stockRepository.save(stock);

        stockMovementRepository.save(
                new StockMovement(
                        prescribedItem.drugId(),
                        prescribedItem.drugName(),
                        StockMovementType.DISPENSE,
                        quantityToDispense,
                        dispenseRecordId,
                        pharmacistId,
                        "Stock deducted during prescription dispensing"
                )
        );

        log.debug(
                "Stock deducted. Drug={}, Quantity={}, DispenseRecord={}",
                prescribedItem.drugName(),
                quantityToDispense,
                dispenseRecordId
        );
    }

    private int normalizePrescribedQuantity(Integer prescribedQuantity) {
        return prescribedQuantity == null || prescribedQuantity <= 0 ? 1 : prescribedQuantity;
    }

    private void publishEvents(DispenseRecord saved) {
        eventPublisher.publishEvent(
                new PrescriptionDispensedEvent(
                        saved.getId(),
                        saved.getPrescriptionId(),
                        saved.getEncounterId(),
                        saved.getPatientId(),
                        saved.getDispensedBy(),
                        saved.isPartial(),
                        saved.getDispensedAt()
                )
        );

        saved.getItems()
                .stream()
                .filter(item -> item.getReason() != null)
                .forEach(item -> eventPublisher.publishEvent(
                        new PrescriptionItemNotDispensedEvent(
                                saved.getPrescriptionId(),
                                item.getPrescriptionItemId(),
                                item.getDrugId(),
                                item.getDrugName(),
                                item.getReason().name(),
                                saved.getDispensedBy(),
                                Instant.now()
                        )
                ));
    }
}