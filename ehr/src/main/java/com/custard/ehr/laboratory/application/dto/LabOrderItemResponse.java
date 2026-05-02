package com.custard.ehr.laboratory.application.dto;

import com.custard.ehr.laboratory.domain.LabOrderItem;

import java.util.UUID;

public record LabOrderItemResponse(
        UUID id,
        UUID labTestId,
        String testName,
        String testCode,
        boolean resulted,
        LabResultResponse result
) {
    public static LabOrderItemResponse from(LabOrderItem item) {
        return new LabOrderItemResponse(
                item.getId(),
                item.getLabTestId(),
                item.getTestName(),
                item.getTestCode(),
                item.hasResult(),
                LabResultResponse.from(item.getResult())
        );
    }
}