package com.custard.ehr.identity.infrastructure;

import com.custard.ehr.identity.application.ports.AppRoleRepository;
import com.custard.ehr.identity.domain.AppRole;
import com.custard.ehr.shared.security.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaAppRoleRepository
        extends JpaRepository<AppRole, UUID>, AppRoleRepository {

    Optional<AppRole> findByName(Role name);

    boolean existsByName(Role name);
}