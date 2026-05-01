package com.custard.ehr.shared.events;

import java.time.Instant;
import java.util.UUID;

public record UserLoggedInEvent(
        UUID userId,
        String username,
        Instant loginAt
) {
}