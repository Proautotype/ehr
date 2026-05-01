package com.custard.ehr.identity.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank String fullName,
        @NotBlank String username,
        @Email @NotBlank String email,
        @Size(min = 8) String password
) {
}
