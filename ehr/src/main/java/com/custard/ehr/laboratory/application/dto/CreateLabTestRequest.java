package com.custard.ehr.laboratory.application.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CreateLabTestRequest(
        @NotBlank String name,
        String code,
        String description,
        BigDecimal price
) {
}