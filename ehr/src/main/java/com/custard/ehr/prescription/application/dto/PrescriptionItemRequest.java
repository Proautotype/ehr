package com.custard.ehr.prescription.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PrescriptionItemRequest(
        @NotNull UUID drugId,
        @NotBlank String dosage,
        @NotBlank String frequency,
        @NotBlank String duration,
        String route,
        Integer quantity,
        String instructions
) {
}