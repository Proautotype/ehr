package com.custard.ehr.shared.infrastruture.auditing;


import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SpringSecurityAuditorAware implements AuditorAware<UUID> {

    @Override
    public Optional<UUID> getCurrentAuditor() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UUID userId) {
            return Optional.of(userId);
        }

        if (principal instanceof String value) {
            try {
                return Optional.of(UUID.fromString(value));
            } catch (IllegalArgumentException ignored) {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }
}