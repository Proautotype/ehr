package com.custard.ehr.identity.application.dto;

import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @Size(min = 8) String newPassword
) {
}