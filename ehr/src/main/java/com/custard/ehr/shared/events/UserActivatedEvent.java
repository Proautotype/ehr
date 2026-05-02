package com.custard.ehr.shared.events;

import java.time.Instant;
import java.util.UUID;

public record UserActivatedEvent(
        UUID userId,
        String username,
        UUID activatedBy,
        Instant activatedAt
) {
}