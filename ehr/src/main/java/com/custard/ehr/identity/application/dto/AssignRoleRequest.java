package com.custard.ehr.identity.application.dto;

import com.custard.ehr.shared.security.Role;
import jakarta.validation.constraints.NotNull;

public record AssignRoleRequest(
        @NotNull Role role
) {
}