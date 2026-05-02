package com.custard.ehr.pharmacy.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record CreateStockItemRequest(
        @NotNull UUID drugId,
        @NotNull @PositiveOrZero Integer openingQuantity
) {
}