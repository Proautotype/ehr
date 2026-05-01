package com.custard.ehr.vitals.application.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record RecordVitalsRequest(
        @NotNull UUID encounterId,
        Integer systolic,
        Integer diastolic,

        BigDecimal weightKg,
        BigDecimal heightCm,

        BigDecimal temperatureCelsius,
        Integer pulseRate,
        Integer respiratoryRate,
        Integer oxygenSaturation,

        String notes
) {
}