package com.custard.ehr.encounter;


import com.custard.ehr.encounter.application.ports.EncounterRepository;
import com.custard.ehr.encounter.domain.Encounter;
import com.custard.ehr.encounter.domain.PaymentStatus;
import com.custard.ehr.shared.events.PaymentStatusChangedEvent;
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


public interface EncounterPaymentUpdater {

    void markPaymentPaid(UUID encounterId);

    void waivePayment(UUID encounterId);
}

@Service
@Slf4j
class EncounterPaymentUpdateService implements EncounterPaymentUpdater {

    private final Logger log = LoggerFactory.getLogger(EncounterPaymentUpdateService.class);

    private final EncounterRepository encounterRepository;
    private final ApplicationEventPublisher eventPublisher;

    EncounterPaymentUpdateService(
            EncounterRepository encounterRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.encounterRepository = encounterRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void markPaymentPaid(UUID encounterId) {
        log.info("Marking encounter {} payment as PAID", encounterId);

        Encounter encounter = getEncounterOrThrow(encounterId);
        PaymentStatus oldStatus = encounter.getPaymentStatus();

        encounter.markPaymentPaid();

        Encounter saved = encounterRepository.save(encounter);

        UUID changedBy = SecurityUtils.requireCurrentUserId();

        eventPublisher.publishEvent(
                new PaymentStatusChangedEvent(
                        saved.getId(),
                        saved.getPatientId(),
                        oldStatus.name(),
                        saved.getPaymentStatus().name(),
                        changedBy,
                        Instant.now()
                )
        );

        log.info(
                "Encounter {} payment status changed from {} to {} by {}",
                encounterId,
                oldStatus,
                saved.getPaymentStatus(),
                changedBy
        );
    }

    @Override
    @Transactional
    public void waivePayment(UUID encounterId) {
        log.info("Waiving payment for encounter {}", encounterId);

        Encounter encounter = getEncounterOrThrow(encounterId);
        PaymentStatus oldStatus = encounter.getPaymentStatus();

        encounter.waivePayment();

        Encounter saved = encounterRepository.save(encounter);

        UUID changedBy = SecurityUtils.requireCurrentUserId();

        eventPublisher.publishEvent(
                new PaymentStatusChangedEvent(
                        saved.getId(),
                        saved.getPatientId(),
                        oldStatus.name(),
                        saved.getPaymentStatus().name(),
                        changedBy,
                        Instant.now()
                )
        );

        log.info(
                "Encounter {} payment status changed from {} to {} by {}",
                encounterId,
                oldStatus,
                saved.getPaymentStatus(),
                changedBy
        );
    }

    private Encounter getEncounterOrThrow(UUID encounterId) {
        return encounterRepository.findById(encounterId)
                .orElseThrow(() -> {
                    log.warn("Payment update failed. Encounter {} not found", encounterId);
                    return new NotFoundException("Encounter not found");
                });
    }
}