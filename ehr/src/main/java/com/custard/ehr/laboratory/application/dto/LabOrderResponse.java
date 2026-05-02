package com.custard.ehr.laboratory.application.dto;

import com.custard.ehr.laboratory.domain.LabOrder;
import com.custard.ehr.laboratory.domain.LabOrderStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record LabOrderResponse(
        UUID id,
        UUID encounterId,
        UUID patientId,
        UUID orderedBy,
        Instant orderedAt,
        LabOrderStatus status,
        String clinicalNote,
        List<LabOrderItemResponse> items
) {
    public static LabOrderResponse from(LabOrder order) {
        return new LabOrderResponse(
                order.getId(),
                order.getEncounterId(),
                order.getPatientId(),
                order.getOrderedBy(),
                order.getOrderedAt(),
                order.getStatus(),
                order.getClinicalNote(),
                order.getItems()
                        .stream()
                        .map(LabOrderItemResponse::from)
                        .toList()
        );
    }
}