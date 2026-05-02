package com.custard.ehr.shared.events;

import java.time.Instant;
import java.util.UUID;

public record UserPasswordResetEvent(
        UUID userId,
        String username,
        UUID resetBy,
        Instant resetAt
) {
}