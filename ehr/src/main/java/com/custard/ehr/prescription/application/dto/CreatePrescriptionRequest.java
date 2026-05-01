package com.custard.ehr.prescription.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreatePrescriptionRequest(
        @NotNull UUID encounterId,
        String notes,

        @NotEmpty
        List<@Valid PrescriptionItemRequest> items
) {
}