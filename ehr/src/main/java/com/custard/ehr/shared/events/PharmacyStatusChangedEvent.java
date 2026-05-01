package com.custard.ehr.shared.events;

import java.time.Instant;
import java.util.UUID;

public record PharmacyStatusChangedEvent(
        UUID encounterId,
        UUID patientId,
        String oldStatus,
        String newStatus,
        UUID by,
        Instant changedAt
) {}