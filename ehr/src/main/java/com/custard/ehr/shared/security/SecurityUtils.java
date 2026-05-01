package com.custard.ehr.shared.security;

import java.util.Optional;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Optional<Authentication> getAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    public static Optional<CurrentUser> getCurrentUser() {
        return getAuthentication()
                .map(Authentication::getPrincipal)
                .filter(CurrentUser.class::isInstance)
                .map(CurrentUser.class::cast);
    }

    public static Optional<UUID> getCurrentUserId() {
        return getCurrentUser().map(CurrentUser::id);
    }

    public static boolean hasRole(Role role) {
        return getCurrentUser()
                .map(user -> user.hasRole(role))
                .orElse(false);
    }

    public static boolean hasPermission(Permission permission) {
        return getCurrentUser()
                .map(user -> user.hasPermission(permission))
                .orElse(false);
    }

    public static UUID requireCurrentUserId() {
        return getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("No authenticated user found"));
    }
}