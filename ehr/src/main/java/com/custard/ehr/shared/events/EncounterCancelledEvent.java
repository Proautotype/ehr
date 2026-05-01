package com.custard.ehr.shared.events;

import java.time.Instant;
import java.util.UUID;

public record EncounterCancelledEvent(
        UUID encounterId,
        UUID patientId,
        UUID cancelledBy,
        Instant cancelledAt
) {}