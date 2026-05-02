package com.custard.ehr.identity.application.ports;

import com.custard.ehr.identity.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findTop50ByOrderByCreatedAtDesc();

    List<User> findTop20ByFullNameContainingIgnoreCaseOrUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String fullName,
            String username,
            String email
    );
}