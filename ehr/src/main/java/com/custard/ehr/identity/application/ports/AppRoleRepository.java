package com.custard.ehr.identity.application.ports;

import com.custard.ehr.identity.domain.AppRole;
import com.custard.ehr.shared.security.Role;

import java.util.Optional;
import java.util.UUID;

public interface AppRoleRepository {

    AppRole save(AppRole role);

    Optional<AppRole> findById(UUID id);

    Optional<AppRole> findByName(Role name);

    boolean existsByName(Role name);
}