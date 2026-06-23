package com.custard.ehr.prescription.application.service;

import com.custard.ehr.pharmacy.DrugIdentifierVerifier;
import com.custard.ehr.pharmacy.DrugLookupView;
import com.custard.ehr.encounter.EncounterIdentifierVerifier;
import com.custard.ehr.encounter.EncounterLookupView;
import com.custard.ehr.prescription.application.dto.CreatePrescriptionRequest;
import com.custard.ehr.prescription.application.dto.PrescriptionResponse;
import com.custard.ehr.prescription.application.ports.PrescriptionRepository;
import com.custard.ehr.prescription.domain.Prescription;
import com.custard.ehr.prescription.domain.PrescriptionStatus;
import com.custard.ehr.shared.events.PrescriptionCreatedEvent;
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
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class PrescriptionService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final PrescriptionRepository prescriptionRepository;
    private final EncounterIdentifierVerifier encounterIdentifierVerifier;
    private final DrugIdentifierVerifier drugIdentifierVerifier;
    private final ApplicationEventPublisher eventPublisher;

    public PrescriptionService(
            PrescriptionRepository prescriptionRepository,
            EncounterIdentifierVerifier encounterIdentifierVerifier,
            DrugIdentifierVerifier drugIdentifierVerifier,
            ApplicationEventPublisher eventPublisher
    ) {
        this.prescriptionRepository = prescriptionRepository;
        this.encounterIdentifierVerifier = encounterIdentifierVerifier;
        this.drugIdentifierVerifier = drugIdentifierVerifier;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public PrescriptionResponse create(CreatePrescriptionRequest request) {
        log.info("Attempting to create prescription for encounter ID: {}", request.encounterId());

        EncounterLookupView encounter = encounterIdentifierVerifier
                .findActiveEncounter(request.encounterId())
                .orElseThrow(() -> {
                    log.warn("Prescription creation blocked. Encounter {} is not active or does not exist", request.encounterId());
                    return new BusinessException("Encounter is not active or does not exist");
                });

        validateNoDuplicateDrugs(request);

        UUID prescribedBy = SecurityUtils.requireCurrentUserId();

        Prescription prescription = new Prescription(
                encounter.encounterId(),
                encounter.patientId(),
                prescribedBy,
                request.notes()
        );

        request.items().forEach(itemRequest -> {
            DrugLookupView drug = drugIdentifierVerifier
                    .findActiveDrug(itemRequest.drugId())
                    .orElseThrow(() -> {
                        log.warn("Prescription creation blocked. Drug {} is inactive or does not exist", itemRequest.drugId());
                        return new BusinessException("Drug does not exist or is inactive");
                    });

            log.debug(
                    "Adding drug {} ({}, {}) to prescription for encounter {}",
                    drug.name(),
                    drug.strength(),
                    drug.form(),
                    encounter.encounterId()
            );

            prescription.addItem(
                    drug.drugId(),
                    drug.name(),
                    drug.strength(),
                    drug.form(),
                    itemRequest.dosage(),
                    itemRequest.frequency(),
                    itemRequest.duration(),
                    itemRequest.route(),
                    itemRequest.quantity(),
                    itemRequest.instructions()
            );
        });

        prescription.sendToPharmacy();

        Prescription saved = prescriptionRepository.save(prescription);

        log.info(
                "Prescription {} created and sent to pharmacy for patient {} under encounter {} by doctor {}",
                saved.getId(),
                saved.getPatientId(),
                saved.getEncounterId(),
                prescribedBy
        );

        eventPublisher.publishEvent(
                new PrescriptionCreatedEvent(
                        saved.getId(),
                        saved.getEncounterId(),
                        saved.getPatientId(),
                        prescribedBy,
                        Instant.now()
                )
        );

        log.debug("PrescriptionCreatedEvent published for prescription ID: {}", saved.getId());

        return PrescriptionResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public PrescriptionResponse getById(UUID prescriptionId) {
        log.debug("Fetching prescription by ID: {}", prescriptionId);

        return prescriptionRepository.findById(prescriptionId)
                .map(PrescriptionResponse::from)
                .orElseThrow(() -> {
                    log.warn("Prescription lookup failed. ID {} not found", prescriptionId);
                    return new NotFoundException("Prescription not found");
                });
    }

    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getByEncounter(UUID encounterId) {
        log.debug("Fetching prescriptions for encounter ID: {}", encounterId);

        return prescriptionRepository.findByEncounterIdOrderByPrescribedAtDesc(encounterId)
                .stream()
                .map(PrescriptionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getByPatient(UUID patientId) {
        log.debug("Fetching prescription history for patient ID: {}", patientId);

        return prescriptionRepository.findByPatientIdOrderByPrescribedAtDesc(patientId)
                .stream()
                .map(PrescriptionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getPharmacyQueue() {
        log.debug("Fetching prescription pharmacy queue");

        return prescriptionRepository
                .findByStatusOrderByPrescribedAtAsc(PrescriptionStatus.SENT_TO_PHARMACY)
                .stream()
                .map(PrescriptionResponse::from)
                .toList();
    }

    @Transactional
    public PrescriptionResponse cancel(UUID prescriptionId) {
        log.info("Attempting to cancel prescription ID: {}", prescriptionId);

        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> {
                    log.warn("Prescription cancellation failed. ID {} not found", prescriptionId);
                    return new NotFoundException("Prescription not found");
                });

        prescription.cancel();

        Prescription saved = prescriptionRepository.save(prescription);

        log.info("Prescription {} cancelled successfully", prescriptionId);

        return PrescriptionResponse.from(saved);
    }

    private void validateNoDuplicateDrugs(CreatePrescriptionRequest request) {
        HashSet<UUID> seenDrugIds = new HashSet<>();

        request.items().forEach(item -> {
            if (!seenDrugIds.add(item.drugId())) {
                log.warn(
                        "Duplicate drug {} detected in prescription request for encounter {}",
                        item.drugId(),
                        request.encounterId()
                );
                throw new BusinessException("Prescription cannot contain duplicate drug items");
            }
        });
    }
}