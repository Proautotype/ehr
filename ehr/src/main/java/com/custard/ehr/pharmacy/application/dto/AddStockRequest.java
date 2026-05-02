package com.custard.ehr.pharmacy.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddStockRequest(
        @NotNull @Positive Integer quantity,
        String note
) {
}