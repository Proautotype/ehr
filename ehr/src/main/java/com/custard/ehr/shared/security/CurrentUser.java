package com.custard.ehr.shared.security;


import java.util.Set;
import java.util.UUID;

public record CurrentUser(
        UUID id,
        String username,
        String fullName,
        Set<Role> roles,
        Set<Permission> permissions
) {

    public boolean hasRole(Role role) {
        return roles != null && roles.contains(role);
    }

    public boolean hasPermission(Permission permission) {
        return permissions != null && permissions.contains(permission);
    }

    public boolean isAdmin() {
        return hasRole(Role.ADMIN);
    }
}