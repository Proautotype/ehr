package com.custard.ehr.encounter;

import com.custard.ehr.encounter.EncounterLabStatusUpdater;
import com.custard.ehr.encounter.application.ports.EncounterRepository;
import com.custard.ehr.encounter.domain.Encounter;
import com.custard.ehr.encounter.domain.LabStatus;
import com.custard.ehr.shared.events.LabStatusChangedEvent;
import com.custard.ehr.shared.exception.NotFoundException;
import com.custard.ehr.shared.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

public interface EncounterLabStatusUpdater {
    void markLabPending(UUID encounterId);
    void markLabCompleted(UUID encounterId);
}


@Service
@Slf4j
class EncounterLabStatusUpdateService implements EncounterLabStatusUpdater {

    private final Logger log = LoggerFactory.getLogger(EncounterLabStatusUpdateService.class);

    private final EncounterRepository encounterRepository;
    private final ApplicationEventPublisher eventPublisher;

    EncounterLabStatusUpdateService(
            EncounterRepository encounterRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.encounterRepository = encounterRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void markLabPending(UUID encounterId) {
        log.info("Marking lab status as PENDING for encounter {}", encounterId);

        Encounter encounter = getEncounterOrThrow(encounterId);
        LabStatus oldStatus = encounter.getLabStatus();

        encounter.markLabPending();
        Encounter saved = encounterRepository.save(encounter);

        publishStatusChanged(saved, oldStatus);

        log.info(
                "Encounter {} lab status changed from {} to {}",
                encounterId,
                oldStatus,
                saved.getLabStatus()
        );
    }

    @Override
    @Transactional
    public void markLabCompleted(UUID encounterId) {
        log.info("Marking lab status as COMPLETED for encounter {}", encounterId);

        Encounter encounter = getEncounterOrThrow(encounterId);
        LabStatus oldStatus = encounter.getLabStatus();

        encounter.markLabCompleted();
        Encounter saved = encounterRepository.save(encounter);

        publishStatusChanged(saved, oldStatus);

        log.info(
                "Encounter {} lab status changed from {} to {}",
                encounterId,
                oldStatus,
                saved.getLabStatus()
        );
    }

    private Encounter getEncounterOrThrow(UUID encounterId) {
        return encounterRepository.findById(encounterId)
                .orElseThrow(() -> {
                    log.warn("Lab status update failed. Encounter {} not found", encounterId);
                    return new NotFoundException("Encounter not found");
                });
    }

    private void publishStatusChanged(Encounter encounter, LabStatus oldStatus) {
        eventPublisher.publishEvent(
                new LabStatusChangedEvent(
                        encounter.getId(),
                        encounter.getPatientId(),
                        oldStatus.name(),
                        encounter.getLabStatus().name(),
                        SecurityUtils.requireCurrentUserId(),
                        Instant.now()
                )
        );
    }
}