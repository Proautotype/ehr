package com.custard.ehr.shared.events;

import java.time.Instant;
import java.util.UUID;

public record PrescriptionItemNotDispensedEvent(
        UUID prescriptionId,
        UUID prescriptionItemId,
        UUID drugId,
        String drugName,
        String reason,
        UUID recordedBy,
        Instant recordedAt
) {
}