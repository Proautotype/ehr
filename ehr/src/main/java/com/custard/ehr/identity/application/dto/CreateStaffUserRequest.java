package com.custard.ehr.identity.application.dto;

import com.custard.ehr.shared.security.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateStaffUserRequest(
        @NotBlank String fullName,
        @NotBlank String username,
        @Email @NotBlank String email,
        @Size(min = 8) String password,
        @NotNull Role role
) {
}