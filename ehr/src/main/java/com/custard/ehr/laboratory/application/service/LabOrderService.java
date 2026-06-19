package com.custard.ehr.laboratory.application.service;

import com.custard.ehr.encounter.EncounterIdentifierVerifier;
import com.custard.ehr.encounter.EncounterLabStatusUpdater;
import com.custard.ehr.encounter.EncounterLookupView;
import com.custard.ehr.identity.IdentityLookupService;
import com.custard.ehr.identity.domain.AppUser;
import com.custard.ehr.laboratory.application.dto.CreateLabOrderRequest;
import com.custard.ehr.laboratory.application.dto.LabOrderResponse;
import com.custard.ehr.laboratory.application.dto.external.LabOrderPatientProjectionResponse;
import com.custard.ehr.laboratory.application.ports.LabOrderRepository;
import com.custard.ehr.laboratory.application.ports.LabTestRepository;
import com.custard.ehr.laboratory.domain.LabOrder;
import com.custard.ehr.laboratory.domain.LabTest;
import com.custard.ehr.shared.events.LabOrderCreatedEvent;
import com.custard.ehr.shared.exception.BusinessException;
import com.custard.ehr.shared.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class LabOrderService {

    private final Logger log = LoggerFactory.getLogger(LabOrderService.class);

    private final LabOrderRepository labOrderRepository;
    private final LabTestRepository labTestRepository;
    private final EncounterIdentifierVerifier encounterIdentifierVerifier;
    private final EncounterLabStatusUpdater encounterLabStatusUpdater;
    private final ApplicationEventPublisher eventPublisher;
    private final LabPatientService labPatientService;
    private final IdentityLookupService identityLookupService;

    public LabOrderService(
            LabOrderRepository labOrderRepository,
            LabTestRepository labTestRepository,
            EncounterIdentifierVerifier encounterIdentifierVerifier,
            EncounterLabStatusUpdater encounterLabStatusUpdater,
            ApplicationEventPublisher eventPublisher,
            LabPatientService labPatientService, IdentityLookupService identityLookupService
    ) {
        this.labOrderRepository = labOrderRepository;
        this.labTestRepository = labTestRepository;
        this.encounterIdentifierVerifier = encounterIdentifierVerifier;
        this.encounterLabStatusUpdater = encounterLabStatusUpdater;
        this.eventPublisher = eventPublisher;
        this.labPatientService = labPatientService;
        this.identityLookupService = identityLookupService;
    }

    @Transactional
    public LabOrderResponse create(CreateLabOrderRequest request) {
        log.info("Creating lab order for encounter {}", request.encounterId());

        validateDuplicateTests(request.labTestIds());

        EncounterLookupView encounter = encounterIdentifierVerifier
                .findActiveEncounter(request.encounterId())
                .orElseThrow(() -> {
                    log.warn("Lab order blocked. Encounter {} is not active or does not exist", request.encounterId());
                    return new BusinessException("Encounter is not active or does not exist");
                });

        User user = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        if (user == null) {
            throw new BusinessException("User is not logged in");
        }

        AppUser userIsNotLoggedIn = identityLookupService.getByUsername(user.getUsername()).orElseThrow(() -> new BusinessException("User is not logged in"));

        LabOrder order = new LabOrder(
                encounter.encounterId(),
                encounter.patientId(),
                userIsNotLoggedIn.getId(),
                request.clinicalNote()
        );

        request.labTestIds().forEach(testId -> {
            LabTest test = labTestRepository.findById(testId)
                    .filter(LabTest::isActive)
                    .orElseThrow(() -> {
                        log.warn("Lab order blocked. Lab test {} is inactive or missing", testId);
                        return new BusinessException("Lab test does not exist or is inactive");
                    });

            order.addItem(test.getId(), test.getName(), test.getCode());
        });

        LabOrder saved = labOrderRepository.save(order);

        encounterLabStatusUpdater.markLabPending(saved.getEncounterId());

        eventPublisher.publishEvent(
                new LabOrderCreatedEvent(
                        saved.getId(),
                        saved.getEncounterId(),
                        saved.getPatientId(),
                        saved.getOrderedBy(),
                        Instant.now()
                )
        );

        log.info(
                "Lab order {} created for patient {} under encounter {}",
                saved.getId(),
                saved.getPatientId(),
                saved.getEncounterId()
        );

        return LabOrderResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public LabOrderResponse getById(UUID id) {
        return labOrderRepository.findById(id)
                .map(LabOrderResponse::from)
                .orElseThrow(() -> new NotFoundException("Lab order not found"));
    }

    @Transactional(readOnly = true)
    public List<LabOrderResponse> getByEncounter(UUID encounterId) {
        return labOrderRepository.findByEncounterIdOrderByOrderedAtDesc(encounterId)
                .stream()
                .map(LabOrderResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LabOrderResponse> getByPatient(UUID patientId) {
        return labOrderRepository.findByPatientIdOrderByOrderedAtDesc(patientId)
                .stream()
                .map(LabOrderResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LabOrderPatientProjectionResponse> findLabOrdersByStatus(String status) {
        List<LabOrderPatientProjectionResponse> byStatus = labPatientService.findByStatus(status);
        log.info("Get lab orders by status {}", byStatus);
        return byStatus;
    }

    private void validateDuplicateTests(List<UUID> labTestIds) {
        HashSet<UUID> seen = new HashSet<>();

        for (UUID id : labTestIds) {
            if (!seen.add(id)) {
                throw new BusinessException("Duplicate lab test in order");
            }
        }
    }
}