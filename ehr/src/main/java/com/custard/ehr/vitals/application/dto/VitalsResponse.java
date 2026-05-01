package com.custard.ehr.vitals.application.dto;


import com.custard.ehr.vitals.domain.Vitals;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record VitalsResponse(
        UUID id,
        UUID encounterId,
        UUID patientId,
        UUID recordedBy,
        Instant recordedAt,

        Integer systolic,
        Integer diastolic,

        BigDecimal weightKg,
        BigDecimal heightCm,
        BigDecimal bmi,

        BigDecimal temperatureCelsius,
        Integer pulseRate,
        Integer respiratoryRate,
        Integer oxygenSaturation,

        String notes
) {
    public static VitalsResponse from(Vitals vitals) {
        return new VitalsResponse(
                vitals.getId(),
                vitals.getEncounterId(),
                vitals.getPatientId(),
                vitals.getRecordedBy(),
                vitals.getRecordedAt(),
                vitals.getSystolic(),
                vitals.getDiastolic(),
                vitals.getWeightKg(),
                vitals.getHeightCm(),
                vitals.getBmi(),
                vitals.getTemperatureCelsius(),
                vitals.getPulseRate(),
                vitals.getRespiratoryRate(),
                vitals.getOxygenSaturation(),
                vitals.getNotes()
        );
    }
}