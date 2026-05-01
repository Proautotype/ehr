package com.custard.ehr.shared.events;

import java.time.Instant;
import java.util.UUID;

public record EncounterCreatedEvent(
        UUID encounterId,
        UUID patientId,
        UUID openedBy,
        Instant openedAt
) {}