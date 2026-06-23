package com.custard.ehr.prescription;

import com.custard.ehr.prescription.application.ports.PrescriptionRepository;
import com.custard.ehr.prescription.domain.Prescription;
import com.custard.ehr.prescription.domain.PrescriptionStatus;
import com.custard.ehr.shared.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


public interface PrescriptionIdentifierVerifier {

    Optional<PrescriptionLookupView> findDispensablePrescription(UUID prescriptionId);

    void markPartiallyDispensed(UUID prescriptionId);

    void markFullyDispensed(UUID prescriptionId);
}

@Service
@Slf4j
class PrescriptionLookupService implements PrescriptionIdentifierVerifier {

    private final Logger log = LoggerFactory.getLogger(PrescriptionLookupService.class);

    private final PrescriptionRepository prescriptionRepository;

    PrescriptionLookupService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PrescriptionLookupView> findDispensablePrescription(UUID prescriptionId) {
        log.debug("Looking up dispensable prescription: {}", prescriptionId);

        return prescriptionRepository.findById(prescriptionId)
                .filter(prescription -> prescription.getStatus() == PrescriptionStatus.SENT_TO_PHARMACY)
                .map(this::toLookupView);
    }

    @Override
    @Transactional
    public void markPartiallyDispensed(UUID prescriptionId) {
        log.info("Marking prescription {} as PARTIALLY_DISPENSED", prescriptionId);

        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new NotFoundException("Prescription not found"));

        prescription.markPartiallyDispensed();
        prescriptionRepository.save(prescription);
    }

    @Override
    @Transactional
    public void markFullyDispensed(UUID prescriptionId) {
        log.info("Marking prescription {} as DISPENSED", prescriptionId);

        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new NotFoundException("Prescription not found"));

        prescription.markDispensed();
        prescriptionRepository.save(prescription);
    }

    private PrescriptionLookupView toLookupView(Prescription prescription) {
        return new PrescriptionLookupView(
                prescription.getId(),
                prescription.getEncounterId(),
                prescription.getPatientId(),
                prescription.getItems()
                        .stream()
                        .map(item -> new PrescriptionItemLookupView(
                                item.getId(),
                                item.getDrugId(),
                                item.getDrugName(),
                                item.getStrength(),
                                item.getForm(),
                                item.getQuantity()
                        ))
                        .toList()
        );
    }
}