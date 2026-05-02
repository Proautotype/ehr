package com.custard.ehr.identity.application.dto;

import com.custard.ehr.identity.domain.User;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record UserResponse(
        UUID id,
        String fullName,
        String username,
        String email,
        boolean active,
        Set<String> roles
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getUsername(),
                user.getEmail(),
                user.isActive(),
                user.getRoles()
                        .stream()
                        .map(userRole -> userRole.getRole().getName().name())
                        .collect(Collectors.toSet())
        );
    }
}