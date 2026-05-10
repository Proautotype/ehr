package com.custard.ehr.shared.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public record CurrentUser(
        UUID id,
        String username,
        String fullName,
        Set<Role> roles,
        Set<Permission> permissions,
        Collection<? extends GrantedAuthority> authorities
) {

    public boolean hasRole(Role role) {
        return roles != null && roles.contains(role);
    }

    public boolean hasPermission(Permission permission) {
        return permissions != null && permissions.contains(permission);
    }

}