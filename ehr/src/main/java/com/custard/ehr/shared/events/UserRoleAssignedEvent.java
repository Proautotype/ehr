package com.custard.ehr.shared.events;

import com.custard.ehr.shared.security.Role;

import java.time.Instant;
import java.util.UUID;

public record UserRoleAssignedEvent(
        UUID userId,
        String username,
        Role role,
        UUID assignedBy,
        Instant assignedAt
) {
}