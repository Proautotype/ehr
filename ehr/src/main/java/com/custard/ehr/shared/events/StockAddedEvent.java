package com.custard.ehr.shared.events;

import java.time.Instant;
import java.util.UUID;

public record StockAddedEvent(
        UUID stockItemId,
        UUID drugId,
        String drugName,
        Integer quantity,
        UUID addedBy,
        Instant addedAt
) {
}