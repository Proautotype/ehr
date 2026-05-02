package com.custard.ehr.shared.events;

import java.time.Instant;
import java.util.UUID;

public record UserDeactivatedEvent(
        UUID userId,
        String username,
        UUID deactivatedBy,
        Instant deactivatedAt
) {
}