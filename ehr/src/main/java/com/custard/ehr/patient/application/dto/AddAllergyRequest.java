package com.custard.ehr.patient.application.dto;

import jakarta.validation.constraints.NotBlank;

public record AddAllergyRequest(
        @NotBlank String name,
        String severity,
        String reaction
) {
}