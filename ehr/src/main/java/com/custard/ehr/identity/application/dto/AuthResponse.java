package com.custard.ehr.identity.application.dto;

import java.util.UUID;

public record AuthResponse(
        UUID userId,
        String username,
        String fullName,
        String accessToken,
        String tokenType,
        String message
) {
}