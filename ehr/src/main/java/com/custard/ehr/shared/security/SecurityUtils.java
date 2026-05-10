package com.custard.ehr.shared.security;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public final class SecurityUtils {

    private static final Logger log = LoggerFactory.getLogger(SecurityUtils.class);

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
        var authentication = getCurrentUser();
        log.info("Current user is present {} ", authentication.isPresent());
        return Optional.of(UUID.randomUUID());
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

    public static User getPrincipal(){
       return (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
    }

}