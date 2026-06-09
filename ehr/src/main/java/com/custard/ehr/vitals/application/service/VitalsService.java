package com.custard.ehr.vitals.application.service;

import com.custard.ehr.encounter.EncounterIdentifierVerifier;
import com.custard.ehr.encounter.EncounterLookupView;
import com.custard.ehr.encounter.domain.EncounterStatus;
import com.custard.ehr.patient.PatientIdentifierVerifier;
import com.custard.ehr.shared.events.VitalsRecordedEvent;
import com.custard.ehr.shared.exception.BusinessException;
import com.custard.ehr.shared.exception.NotFoundException;
import com.custard.ehr.shared.security.SecurityUtils;
import com.custard.ehr.vitals.application.dto.RecordVitalsRequest;
import com.custard.ehr.vitals.application.dto.UpdateVitalsRequest;
import com.custard.ehr.vitals.application.dto.VitalsResponse;
import com.custard.ehr.vitals.application.mapper.VitalsMapper;
import com.custard.ehr.vitals.application.ports.VitalsRepository;
import com.custard.ehr.vitals.domain.Vitals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class VitalsService {

    private final Logger log = LoggerFactory.getLogger(VitalsService.class);

    private final VitalsRepository vitalsRepository;
    private final PatientIdentifierVerifier patientIdentifierVerifier;
    private final EncounterIdentifierVerifier encounterIdentifierVerifier;
    private final ApplicationEventPublisher eventPublisher;

    public VitalsService(
            VitalsRepository vitalsRepository,
            PatientIdentifierVerifier patientIdentifierVerifier,
            EncounterIdentifierVerifier encounterIdentifierVerifier,
            ApplicationEventPublisher eventPublisher
    ) {
        this.vitalsRepository = vitalsRepository;
        this.patientIdentifierVerifier = patientIdentifierVerifier;
        this.encounterIdentifierVerifier = encounterIdentifierVerifier;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public VitalsResponse recordVitals(RecordVitalsRequest request) {

        EncounterLookupView encounter = encounterIdentifierVerifier
                .findActiveEncounter(request.encounterId())
                .orElseThrow(() -> {
                    log.warn("Vitals recording blocked. Encounter {} is not active or does not exist", request.encounterId());
                    return new BusinessException("Encounter is not active or does not exist");
                });
        log.info(
                "Attempting to record vitals for patient ID: {} under encounter ID: {}",
                encounter.patientId(),
                encounter.encounterId()
        );

        validatePatient(encounter.patientId());
        validateEncounter(encounter.encounterId());

        UUID recordedBy = SecurityUtils.requireCurrentUserId();

        Vitals vitals = new Vitals(
                request.encounterId(),
                encounter.patientId(),
                recordedBy,
                request.systolic(),
                request.diastolic(),
                request.weightKg(),
                request.heightCm(),
                request.temperatureCelsius(),
                request.pulseRate(),
                request.respiratoryRate(),
                request.oxygenSaturation(),
                request.notes()
        );

        Vitals saved = vitalsRepository.save(vitals);

        log.info(
                "Vitals {} recorded successfully for patient {} under encounter {} by user {}",
                saved.getId(),
                saved.getPatientId(),
                saved.getEncounterId(),
                recordedBy
        );

        eventPublisher.publishEvent(
                new VitalsRecordedEvent(
                        saved.getId(),
                        saved.getEncounterId(),
                        saved.getPatientId(),
                        recordedBy,
                        Instant.now()
                )
        );

        log.debug("VitalsRecordedEvent published for vitals ID: {}", saved.getId());

        return VitalsResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public VitalsResponse getById(UUID vitalsId) {
        log.debug("Fetching vitals by ID: {}", vitalsId);

        return vitalsRepository.findById(vitalsId)
                .map(VitalsResponse::from)
                .orElseThrow(() -> {
                    log.warn("Vitals lookup failed. ID {} not found", vitalsId);
                    return new NotFoundException("Vitals record not found");
                });
    }

    @Transactional(readOnly = true)
    public VitalsResponse getLatestByEncounter(UUID encounterId) {
        log.debug("Fetching latest vitals for encounter ID: {}", encounterId);

        return vitalsRepository.findTopByEncounterIdOrderByRecordedAtDesc(encounterId)
                .map(VitalsResponse::from)
                .orElseThrow(() -> {
                    log.warn("No vitals found for encounter ID: {}", encounterId);
                    return new NotFoundException("No vitals found for encounter");
                });
    }

    @Transactional(readOnly = true)
    public List<VitalsResponse> getByEncounter(UUID encounterId) {
        log.debug("Fetching all vitals for encounter ID: {}", encounterId);

        return vitalsRepository.findByEncounterIdOrderByRecordedAtDesc(encounterId)
                .stream()
                .map(VitalsResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VitalsResponse> getByPatient(UUID patientId) {
        log.debug("Fetching vitals history for patient ID: {}", patientId);

        return vitalsRepository.findByPatientIdOrderByRecordedAtDesc(patientId)
                .stream()
                .map(VitalsResponse::from)
                .toList();
    }

    private void validatePatient(UUID patientId) {
        log.debug("Validating patient before recording vitals. Patient ID: {}", patientId);

        if (!patientIdentifierVerifier.isValid(patientId)) {
            log.warn("Vitals recording blocked. Patient {} does not exist", patientId);
            throw new BusinessException("Patient does not exist");
        }
    }

    private void validateEncounter(UUID encounterId) {
        log.debug("Validating active encounter before recording vitals. Encounter ID: {}", encounterId);

        if (!encounterIdentifierVerifier.isActive(encounterId)) {
            log.warn("Vitals recording blocked. Encounter {} is not active or does not exist", encounterId);
            throw new BusinessException("Encounter is not active or does not exist");
        }
    }

    @Transactional
    public boolean updateRecord(UUID encounterId, UpdateVitalsRequest request) {

        // lookup encounter
        EncounterStatus status = encounterIdentifierVerifier
                .getStatus(encounterId);

        if(status != EncounterStatus.ACTIVE){
            throw new BusinessException("You cannot edit an inactive process");
        }

        vitalsRepository.findByEncounterId(encounterId).ifPresent(vitals -> {
            VitalsMapper.toUpdate(vitals, request);
            vitalsRepository.save(vitals);
        });
        return true;
    }
}