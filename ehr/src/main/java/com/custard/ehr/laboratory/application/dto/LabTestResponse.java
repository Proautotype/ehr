package com.custard.ehr.laboratory.application.dto;

import com.custard.ehr.laboratory.domain.LabTest;

import java.math.BigDecimal;
import java.util.UUID;

public record LabTestResponse(
        UUID id,
        String name,
        String code,
        String description,
        BigDecimal price,
        boolean active
) {
    public static LabTestResponse from(LabTest test) {
        return new LabTestResponse(
                test.getId(),
                test.getName(),
                test.getCode(),
                test.getDescription(),
                test.getPrice(),
                test.isActive()
        );
    }
}