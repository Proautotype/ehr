package com.custard.ehr.laboratory.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RecordLabResultRequest(
        @NotNull UUID labOrderItemId,
        @NotBlank String resultValue,
        String referenceRange,
        String interpretation,
        String clinicalNotes
) {
}