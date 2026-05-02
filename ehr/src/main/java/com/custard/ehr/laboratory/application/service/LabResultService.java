package com.custard.ehr.laboratory.application.service;

import com.custard.ehr.encounter.EncounterLabStatusUpdater;
import com.custard.ehr.laboratory.application.dto.LabOrderResponse;
import com.custard.ehr.laboratory.application.dto.RecordLabResultRequest;
import com.custard.ehr.laboratory.application.ports.LabOrderRepository;
import com.custard.ehr.laboratory.domain.LabOrder;
import com.custard.ehr.laboratory.domain.LabOrderItem;
import com.custard.ehr.laboratory.domain.LabOrderStatus;
import com.custard.ehr.shared.events.LabResultRecordedEvent;
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
import java.util.UUID;

@Service
@Slf4j
public class LabResultService {

    private final Logger log = LoggerFactory.getLogger(LabResultService.class);

    private final LabOrderRepository labOrderRepository;
    private final EncounterLabStatusUpdater encounterLabStatusUpdater;
    private final ApplicationEventPublisher eventPublisher;

    public LabResultService(
            LabOrderRepository labOrderRepository,
            EncounterLabStatusUpdater encounterLabStatusUpdater,
            ApplicationEventPublisher eventPublisher
    ) {
        this.labOrderRepository = labOrderRepository;
        this.encounterLabStatusUpdater = encounterLabStatusUpdater;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public LabOrderResponse recordResult(UUID labOrderId, RecordLabResultRequest request) {
        log.info(
                "Recording lab result for lab order {}, item {}",
                labOrderId,
                request.labOrderItemId()
        );

        LabOrder order = labOrderRepository.findById(labOrderId)
                .orElseThrow(() -> new NotFoundException("Lab order not found"));

        if (order.getStatus() == LabOrderStatus.CANCELLED) {
            throw new BusinessException("Cannot record result for cancelled lab order");
        }

        LabOrderItem item = order.getItems()
                .stream()
                .filter(orderItem -> orderItem.getId().equals(request.labOrderItemId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Lab order item not found"));

        if (item.hasResult()) {
            throw new BusinessException("Result already recorded for this lab order item");
        }

        UUID recordedBy = SecurityUtils.requireCurrentUserId();

        item.recordResult(
                request.resultValue(),
                request.unit(),
                request.referenceRange(),
                request.interpretation(),
                recordedBy
        );

        order.markResulted();

        LabOrder saved = labOrderRepository.save(order);

        boolean completed = saved.getStatus() == LabOrderStatus.COMPLETED;

        if (completed) {
            encounterLabStatusUpdater.markLabCompleted(saved.getEncounterId());
        }

        eventPublisher.publishEvent(
                new LabResultRecordedEvent(
                        saved.getId(),
                        item.getId(),
                        saved.getEncounterId(),
                        saved.getPatientId(),
                        recordedBy,
                        completed,
                        Instant.now()
                )
        );

        log.info(
                "Lab result recorded for order {} item {}. Order status={}",
                saved.getId(),
                item.getId(),
                saved.getStatus()
        );

        return LabOrderResponse.from(saved);
    }
}