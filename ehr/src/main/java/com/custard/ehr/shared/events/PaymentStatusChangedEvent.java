package com.custard.ehr.shared.events;

import java.time.Instant;
import java.util.UUID;

public record PaymentStatusChangedEvent(
        UUID encounterId,
        UUID patientId,
        String oldStatus,
        String newStatus,
        UUID changedBy,
        Instant changedAt
) {
}