package com.custard.ehr.consultation.application.service;

import com.custard.ehr.consultation.application.dto.ConsultationResponse;
import com.custard.ehr.consultation.application.dto.CreateConsultationRequest;
import com.custard.ehr.consultation.application.ports.ConsultationRepository;
import com.custard.ehr.consultation.domain.ConsultationNote;
import com.custard.ehr.encounter.EncounterIdentifierVerifier;
import com.custard.ehr.shared.events.ConsultationRecordedEvent;
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
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ConsultationService {

    private final Logger log = LoggerFactory.getLogger(ConsultationService.class);

    private final ConsultationRepository consultationRepository;
    private final EncounterIdentifierVerifier encounterIdentifierVerifier;
    private final ApplicationEventPublisher eventPublisher;

    public ConsultationService(
            ConsultationRepository consultationRepository,
            EncounterIdentifierVerifier encounterIdentifierVerifier,
            ApplicationEventPublisher eventPublisher
    ) {
        this.consultationRepository = consultationRepository;
        this.encounterIdentifierVerifier = encounterIdentifierVerifier;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ConsultationResponse recordConsult(CreateConsultationRequest request) {
        log.info("Attempting to record consultation for encounter ID: {}", request.encounterId());

        EncounterLookupView encounter = encounterIdentifierVerifier
                .findActiveEncounter(request.encounterId())
                .orElseThrow(() -> {
                    log.warn("Consultation recording blocked. Encounter {} is not active or does not exist", request.encounterId());
                    return new BusinessException("Encounter is not active or does not exist");
                });

        UUID doctorId = SecurityUtils.requireCurrentUserId();

        ConsultationNote note = new ConsultationNote(
                encounter.encounterId(),
                encounter.patientId(),
                doctorId,
                request.symptoms(),
                request.diagnosis(),
                request.clinicalNotes(),
                request.treatmentPlan(),
                request.followUpInstructions()
        );

        ConsultationNote saved = consultationRepository.save(note);

        log.info(
                "Consultation {} recorded successfully for patient {} under encounter {} by doctor {}",
                saved.getId(),
                saved.getPatientId(),
                saved.getEncounterId(),
                doctorId
        );

        eventPublisher.publishEvent(
                new ConsultationRecordedEvent(
                        saved.getId(),
                        saved.getEncounterId(),
                        saved.getPatientId(),
                        doctorId,
                        Instant.now()
                )
        );

        log.debug("ConsultationRecordedEvent published for consultation ID: {}", saved.getId());

        return ConsultationResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public ConsultationResponse getById(UUID consultationId) {
        log.debug("Fetching consultation by ID: {}", consultationId);

        return consultationRepository.findById(consultationId)
                .map(ConsultationResponse::from)
                .orElseThrow(() -> {
                    log.warn("Consultation lookup failed. ID {} not found", consultationId);
                    return new NotFoundException("Consultation record not found");
                });
    }

    @Transactional(readOnly = true)
    public ConsultationResponse getLatestByEncounter(UUID encounterId) {
        log.debug("Fetching latest consultation for encounter ID: {}", encounterId);

        return consultationRepository.findTopByEncounterIdOrderByRecordedAtDesc(encounterId)
                .map(ConsultationResponse::from)
                .orElseThrow(() -> {
                    log.warn("No consultation found for encounter ID: {}", encounterId);
                    return new NotFoundException("No consultation found for encounter");
                });
    }

    @Transactional(readOnly = true)
    public List<ConsultationResponse> getByEncounter(UUID encounterId) {
        log.debug("Fetching consultation history for encounter ID: {}", encounterId);

        return consultationRepository.findByEncounterIdOrderByRecordedAtDesc(encounterId)
                .stream()
                .map(ConsultationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ConsultationResponse> getByPatient(UUID patientId) {
        log.debug("Fetching consultation history for patient ID: {}", patientId);

        return consultationRepository.findByPatientIdOrderByRecordedAtDesc(patientId)
                .stream()
                .map(ConsultationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ConsultationResponse> getByDoctor(UUID doctorId) {
        log.debug("Fetching consultation history for doctor ID: {}", doctorId);

        return consultationRepository.findByDoctorIdOrderByRecordedAtDesc(doctorId)
                .stream()
                .map(ConsultationResponse::from)
                .toList();
    }
}