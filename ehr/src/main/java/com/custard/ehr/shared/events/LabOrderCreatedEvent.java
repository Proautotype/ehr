package com.custard.ehr.shared.events;

import java.time.Instant;
import java.util.UUID;

public record LabOrderCreatedEvent(
        UUID labOrderId,
        UUID encounterId,
        UUID patientId,
        UUID orderedBy,
        Instant orderedAt
) {
}