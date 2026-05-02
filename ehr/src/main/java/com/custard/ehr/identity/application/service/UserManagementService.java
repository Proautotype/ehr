package com.custard.ehr.identity.application.service;

import com.custard.ehr.identity.application.dto.AssignRoleRequest;
import com.custard.ehr.identity.application.dto.CreateStaffUserRequest;
import com.custard.ehr.identity.application.dto.ResetPasswordRequest;
import com.custard.ehr.identity.application.dto.UserResponse;
import com.custard.ehr.identity.application.ports.AppRoleRepository;
import com.custard.ehr.identity.application.ports.PasswordHasher;
import com.custard.ehr.identity.application.ports.UserRepository;
import com.custard.ehr.identity.domain.AppRole;
import com.custard.ehr.identity.domain.User;
import com.custard.ehr.shared.events.UserActivatedEvent;
import com.custard.ehr.shared.events.UserDeactivatedEvent;
import com.custard.ehr.shared.events.UserPasswordResetEvent;
import com.custard.ehr.shared.events.UserRegisteredEvent;
import com.custard.ehr.shared.events.UserRoleAssignedEvent;
import com.custard.ehr.shared.exception.BusinessException;
import com.custard.ehr.shared.exception.NotFoundException;
import com.custard.ehr.shared.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserManagementService {

    private final Logger log = LoggerFactory.getLogger(UserManagementService.class);

    private final UserRepository userRepository;
    private final AppRoleRepository appRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public UserManagementService(
            UserRepository userRepository,
            AppRoleRepository appRoleRepository,
            PasswordEncoder passwordEncoder,
            ApplicationEventPublisher eventPublisher
    ) {
        this.userRepository = userRepository;
        this.appRoleRepository = appRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public UserResponse createStaffUser(CreateStaffUserRequest request) {
        log.info("Creating staff user with username {}", request.username());

        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException("Username already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email already exists");
        }

        AppRole role = appRoleRepository.findByName(request.role())
                .orElseThrow(() -> new BusinessException("Role has not been initialized: " + request.role()));

        User user = new User(
                request.fullName(),
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password())
        );

        user.assignRole(role);

        User saved = userRepository.save(user);
        UUID actorId = SecurityUtils.requireCurrentUserId();

        eventPublisher.publishEvent(
                new UserRegisteredEvent(
                        saved.getId(),
                        saved.getUsername(),
                        saved.getEmail(),
                        Instant.now()
                )
        );

        eventPublisher.publishEvent(
                new UserRoleAssignedEvent(
                        saved.getId(),
                        saved.getUsername(),
                        request.role(),
                        actorId,
                        Instant.now()
                )
        );

        log.info("Staff user {} created with role {}", saved.getId(), request.role());

        return UserResponse.from(saved);
    }

    @Transactional
    public UserResponse assignRole(UUID userId, AssignRoleRequest request) {
        log.info("Assigning role {} to user {}", request.role(), userId);

        User user = getUserOrThrow(userId);

        AppRole role = appRoleRepository.findByName(request.role())
                .orElseThrow(() -> new BusinessException("Role has not been initialized: " + request.role()));

        user.assignRole(role);

        User saved = userRepository.save(user);

        eventPublisher.publishEvent(
                new UserRoleAssignedEvent(
                        saved.getId(),
                        saved.getUsername(),
                        request.role(),
                        SecurityUtils.requireCurrentUserId(),
                        Instant.now()
                )
        );

        return UserResponse.from(saved);
    }

    @Transactional
    public UserResponse activate(UUID userId) {
        log.info("Activating user {}", userId);

        User user = getUserOrThrow(userId);
        user.activate();

        User saved = userRepository.save(user);

        eventPublisher.publishEvent(
                new UserActivatedEvent(
                        saved.getId(),
                        saved.getUsername(),
                        SecurityUtils.requireCurrentUserId(),
                        Instant.now()
                )
        );

        return UserResponse.from(saved);
    }

    @Transactional
    public UserResponse deactivate(UUID userId) {
        log.info("Deactivating user {}", userId);

        User user = getUserOrThrow(userId);
        user.deactivate();

        User saved = userRepository.save(user);

        eventPublisher.publishEvent(
                new UserDeactivatedEvent(
                        saved.getId(),
                        saved.getUsername(),
                        SecurityUtils.requireCurrentUserId(),
                        Instant.now()
                )
        );

        return UserResponse.from(saved);
    }

    @Transactional
    public void resetPassword(UUID userId, ResetPasswordRequest request) {
        log.info("Resetting password for user {}", userId);

        User user = getUserOrThrow(userId);
        user.resetPassword(passwordEncoder.encode(request.newPassword()));

        User saved = userRepository.save(user);

        eventPublisher.publishEvent(
                new UserPasswordResetEvent(
                        saved.getId(),
                        saved.getUsername(),
                        SecurityUtils.requireCurrentUserId(),
                        Instant.now()
                )
        );
    }

    @Transactional(readOnly = true)
    public UserResponse getById(UUID userId) {
        return UserResponse.from(getUserOrThrow(userId));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> recentUsers() {
        return userRepository.findTop50ByOrderByCreatedAtDesc()
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserResponse> search(String query) {
        return userRepository
                .findTop20ByFullNameContainingIgnoreCaseOrUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        query,
                        query,
                        query
                )
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User lookup failed. User {} not found", userId);
                    return new NotFoundException("User not found");
                });
    }
}