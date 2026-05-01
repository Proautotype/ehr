package com.custard.ehr.encounter.application.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateEncounterRequest(
        @NotNull UUID patientId,
        String reasonForVisit
) {
}