package com.custard.ehr.shared.events;

import java.time.Instant;
import java.util.UUID;

public record LabResultRecordedEvent(
        UUID labOrderId,
        UUID labOrderItemId,
        UUID encounterId,
        UUID patientId,
        UUID recordedBy,
        boolean orderCompleted,
        Instant recordedAt
) {
}