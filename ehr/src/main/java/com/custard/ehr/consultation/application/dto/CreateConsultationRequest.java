package com.custard.ehr.consultation.application.dto;


import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateConsultationRequest(
        @NotNull UUID encounterId,
        String symptoms,
        String diagnosis,
        String clinicalNotes,
        String treatmentPlan,
        String followUpInstructions
) {
}