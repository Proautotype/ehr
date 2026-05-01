package com.custard.ehr.pharmacy.application.dto;

import com.custard.ehr.pharmacy.domain.NonDispenseReason;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DispenseItemRequest(
        @NotNull UUID prescriptionItemId,
        @NotNull Integer quantityToDispense,
        NonDispenseReason reason,
        String note
) {
}