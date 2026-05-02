package com.custard.ehr.pharmacy.application.dto;

import com.custard.ehr.pharmacy.domain.StockItem;

import java.util.UUID;

public record StockItemResponse(
        UUID id,
        UUID drugId,
        String drugName,
        String strength,
        String form,
        Integer quantityAvailable
) {
    public static StockItemResponse from(StockItem item) {
        return new StockItemResponse(
                item.getId(),
                item.getDrugId(),
                item.getDrugName(),
                item.getStrength(),
                item.getForm(),
                item.getQuantityAvailable()
        );
    }
}