package com.custard.ehr.shared.events;

import java.time.Instant;
import java.util.UUID;

public record EncounterCompletedEvent(
        UUID encounterId,
        UUID patientId,
        UUID completedBy,
        Instant completedAt
) {
}