package com.custard.ehr.identity.infrastructure;

import com.custard.ehr.identity.application.ports.UserRepository;
import com.custard.ehr.identity.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaUserRepository extends JpaRepository<AppUser, UUID>, UserRepository {

    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

}
