package com.custard.ehr.identity.application.ports;

import com.custard.ehr.identity.domain.AppUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    AppUser save(AppUser user);

    Optional<AppUser> findById(UUID id);

    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<AppUser> findTop50ByOrderByCreatedAtDesc();

    List<AppUser> findTop20ByFullNameContainingIgnoreCaseOrUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String fullName,
            String username,
            String email
    );
}