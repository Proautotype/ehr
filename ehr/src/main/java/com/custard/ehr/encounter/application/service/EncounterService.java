package com.custard.ehr.encounter.application.service;

import com.custard.ehr.encounter.application.dto.CreateEncounterRequest;
import com.custard.ehr.encounter.application.dto.EncounterResponse;
import com.custard.ehr.encounter.application.ports.EncounterRepository;
import com.custard.ehr.encounter.domain.*;
import com.custard.ehr.patient.PatientIdentifierVerifier;
import com.custard.ehr.shared.events.*;
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

@Slf4j // Added annotation
@Service
public class EncounterService {

    private final Logger log = LoggerFactory.getLogger(EncounterService.class);

    private final EncounterRepository encounterRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PatientIdentifierVerifier patientVerifier;

    public EncounterService(
            EncounterRepository encounterRepository,
            ApplicationEventPublisher eventPublisher, PatientIdentifierVerifier patientVerifier
    ) {
        this.encounterRepository = encounterRepository;
        this.eventPublisher = eventPublisher;
        this.patientVerifier = patientVerifier;
    }

    @Transactional
    public EncounterResponse create(CreateEncounterRequest request) {
        log.info("Attempting to open new encounter for patient ID: {}", request.patientId());

        if(!patientVerifier.isValid(request.patientId())) {
            log.info("Patient ID {} is not valid", request.patientId());
            throw new BusinessException("Invalid patient ID");
        }

        if (encounterRepository.existsByPatientIdAndStatus(
                request.patientId(),
                EncounterStatus.ACTIVE
        )) {
            log.warn("Encounter creation blocked: Patient {} already has an ACTIVE encounter", request.patientId());
            throw new BusinessException("Patient already has an active encounter");
        }

        UUID openedBy = SecurityUtils.requireCurrentUserId();
        Encounter encounter = new Encounter(
                request.patientId(),
                openedBy,
                request.reasonForVisit()
        );

        Encounter saved = encounterRepository.save(encounter);
        log.info("Encounter {} successfully opened by user {}", saved.getId(), openedBy);

        eventPublisher.publishEvent(
                new EncounterCreatedEvent(
                        saved.getId(),
                        saved.getPatientId(),
                        openedBy,
                        saved.getOpenedAt()
                )
        );

        return EncounterResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public EncounterResponse getById(UUID id) {
        log.debug("Fetching encounter details for ID: {}", id);
        return encounterRepository.findById(id)
                .map(EncounterResponse::from)
                .orElseThrow(() -> {
                    log.warn("Encounter lookup failed: ID {} not found", id);
                    return new NotFoundException("Encounter not found");
                });
    }

    @Transactional(readOnly = true)
    public List<EncounterResponse> getByPatient(UUID patientId) {
        log.debug("Fetching encounter history for patient ID: {}", patientId);
        return encounterRepository.findByPatientIdOrderByOpenedAtDesc(patientId)
                .stream()
                .map(EncounterResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EncounterResponse> getActiveEncounters() {
        log.debug("Fetching all currently active encounters");
        return encounterRepository.findByStatusOrderByOpenedAtDesc(EncounterStatus.ACTIVE)
                .stream()
                .map(EncounterResponse::from)
                .toList();
    }

    @Transactional
    public EncounterResponse complete(UUID encounterId) {
        log.info("Attempting to complete encounter ID: {}", encounterId);

        Encounter encounter = encounterRepository.findById(encounterId)
                .orElseThrow(() -> {
                    log.warn("Completion failed: Encounter {} not found", encounterId);
                    return new NotFoundException("Encounter not found");
                });

        UUID completedBy = SecurityUtils.requireCurrentUserId();
        encounter.complete(completedBy);

        Encounter saved = encounterRepository.save(encounter);
        log.info("Encounter {} marked as COMPLETED by user {}", saved.getId(), completedBy);

        eventPublisher.publishEvent(
                new EncounterCompletedEvent(
                        saved.getId(),
                        saved.getPatientId(),
                        completedBy,
                        Instant.now()
                )
        );
        log.debug("EncounterCompletedEvent published for encounter ID: {}", saved.getId());

        return EncounterResponse.from(saved);
    }

    @Transactional
    public EncounterResponse cancel(UUID encounterId) {
        log.info("Cancelling encounter ID: {}", encounterId);
        Encounter encounter = getEncounterOrThrow(encounterId);

        encounter.cancel();
        Encounter saved = encounterRepository.save(encounter);
        log.info("Encounter {} successfully CANCELLED", encounterId);

        eventPublisher.publishEvent(
                new EncounterCancelledEvent(
                        encounter.getId(),
                        encounter.getPatientId(),
                        SecurityUtils.requireCurrentUserId(),
                        Instant.now()
                )
        );

        return EncounterResponse.from(saved);
    }

    @Transactional
    public EncounterResponse markPaymentPaid(UUID encounterId) {
        log.info("Updating payment status: PAID for encounter {}", encounterId);

        Encounter encounter = getEncounterOrThrow(encounterId);
        if(encounter.getStatus() == EncounterStatus.CANCELLED){
            log.info("MARK PAID: Encounter {} has been cancelled", encounterId);
            throw new  BusinessException(String.format("Cannot continue because encounter %s is CANCELLED", encounterId));
        }

        if(encounter.getPaymentStatus() == PaymentStatus.PAID){
            log.info("MARK PAID: Encounter {} has been paid", encounterId);
            throw new BusinessException(String.format("Cannot continue because encounter %s is PAID", encounterId));
        }


        PaymentStatus oldStatus = encounter.getPaymentStatus();

        encounter.markPaymentPaid();
        Encounter saved = encounterRepository.save(encounter);

        log.info("Update payment status: PAID for encounter {}", encounterId);

        eventPublisher.publishEvent(
                new PaymentStatusChangedEvent(
                        saved.getId(),
                        saved.getPatientId(),
                        oldStatus.name(),
                        saved.getPaymentStatus().name(),
                        SecurityUtils.requireCurrentUserId(),
                        Instant.now()
                )
        );

        return EncounterResponse.from(saved);
    }

    @Transactional
    public EncounterResponse waivePayment(UUID encounterId) {
        log.info("Updating payment status: WAIVED for encounter {}", encounterId);

        Encounter encounter = getEncounterOrThrow(encounterId);
        PaymentStatus oldStatus = encounter.getPaymentStatus();

        encounter.waivePayment();
        Encounter saved = encounterRepository.save(encounter);

        eventPublisher.publishEvent(
                new PaymentStatusChangedEvent(
                        saved.getId(),
                        saved.getPatientId(),
                        oldStatus.name(),
                        saved.getPaymentStatus().name(),
                        SecurityUtils.requireCurrentUserId(),
                        Instant.now()
                )
        );

        return EncounterResponse.from(saved);
    }

    @Transactional
    public EncounterResponse markLabPending(UUID encounterId) {
        log.debug("Setting lab status to PENDING for encounter {}", encounterId);

        Encounter encounter = getEncounterOrThrow(encounterId);
        LabStatus oldStatus = encounter.getLabStatus();

        encounter.markLabPending();
        Encounter saved = encounterRepository.save(encounter);

        log.debug("Set lab status to PENDING for encounter {}", encounterId);

        eventPublisher.publishEvent(
                new LabStatusChangedEvent(
                        saved.getId(),
                        saved.getPatientId(),
                        oldStatus.name(),
                        saved.getLabStatus().name(),
                        SecurityUtils.requireCurrentUserId(),
                        Instant.now()
                )
        );

        return EncounterResponse.from(saved);
    }

    @Transactional
    public EncounterResponse markLabCompleted(UUID encounterId) {
        log.info("Lab tests COMPLETED for encounter {}", encounterId);

        Encounter encounter = getEncounterOrThrow(encounterId);
        if(encounter.getStatus() == EncounterStatus.CANCELLED){
            log.info("MARK COMPLETED: Encounter {} has been cancelled", encounterId);
            throw new  BusinessException(String.format("Cannot continue because encounter %s is CANCELLED", encounterId));
        }


        LabStatus oldStatus = encounter.getLabStatus();

        encounter.markLabCompleted();
        Encounter saved = encounterRepository.save(encounter);

        eventPublisher.publishEvent(
                new LabStatusChangedEvent(
                        saved.getId(),
                        saved.getPatientId(),
                        oldStatus.name(),
                        saved.getLabStatus().name(),
                        SecurityUtils.requireCurrentUserId(),
                        Instant.now()
                )
        );

        return EncounterResponse.from(saved);
    }

    @Transactional
    public EncounterResponse markPharmacyPending(UUID encounterId) {
        log.debug("Setting pharmacy status to PENDING for encounter {}", encounterId);

        Encounter encounter = getEncounterOrThrow(encounterId);
        PharmacyStatus oldStatus = encounter.getPharmacyStatus();

        encounter.markPharmacyPending();
        Encounter saved = encounterRepository.save(encounter);

        eventPublisher.publishEvent(
                new PharmacyStatusChangedEvent(
                        saved.getId(),
                        saved.getPatientId(),
                        oldStatus.name(),
                        saved.getPharmacyStatus().name(),
                        SecurityUtils.requireCurrentUserId(),
                        Instant.now()
                )
        );

        return EncounterResponse.from(saved);
    }

    @Transactional
    public EncounterResponse markPharmacyDispensed(UUID encounterId) {
        log.info("Pharmacy items DISPENSED for encounter {}", encounterId);

        Encounter encounter = getEncounterOrThrow(encounterId);
        PharmacyStatus oldStatus = encounter.getPharmacyStatus();

        encounter.markPharmacyDispensed();
        Encounter saved = encounterRepository.save(encounter);

        log.info("Pharmacy items DISPENSED for encounter {} completed", encounterId);

        eventPublisher.publishEvent(
                new PharmacyStatusChangedEvent(
                        saved.getId(),
                        saved.getPatientId(),
                        oldStatus.name(),
                        saved.getPharmacyStatus().name(),
                        SecurityUtils.requireCurrentUserId(),
                        Instant.now()
                )
        );

        return EncounterResponse.from(saved);
    }

    private Encounter getEncounterOrThrow(UUID encounterId) {
        return encounterRepository.findById(encounterId)
                .orElseThrow(() -> {
                    log.warn("Operation failed: Encounter {} not found", encounterId);
                    return new NotFoundException("Encounter not found");
                });
    }
}