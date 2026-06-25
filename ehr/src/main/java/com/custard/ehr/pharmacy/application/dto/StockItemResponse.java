package com.custard.ehr.pharmacy.application.dto;

import com.custard.ehr.pharmacy.domain.StockItem;

import java.util.UUID;

public record StockItemResponse(
        UUID id,
        UUID productId,
        String productName,
        Integer quantityAvailable
) {
    public static StockItemResponse from(StockItem item) {
        return new StockItemResponse(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getQuantityAvailable()
        );
    }
}