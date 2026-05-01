package com.custard.ehr.identity.application.service;

import com.custard.ehr.identity.application.dto.RegisterUserRequest;
import com.custard.ehr.identity.application.ports.UserRepository;
import com.custard.ehr.identity.domain.User;
import com.custard.ehr.shared.events.UserRegisteredEvent;
import com.custard.ehr.shared.exception.BusinessException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;

@Service
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ApplicationEventPublisher eventPublisher
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public User register(RegisterUserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            logger.error("Username {} already exists",  request.username());
            throw new BusinessException("Username already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            logger.error("Email {} already exists",  request.email());
            throw new BusinessException("Email already exists");
        }

        String passwordHash = passwordEncoder.encode(request.password());

        User user = new User(
                request.fullName(),
                request.username(),
                request.email(),
                passwordHash
        );


        userRepository.save(user);

        logger.info("User {} registered successfully", user.getId());
        eventPublisher.publishEvent(
                new UserRegisteredEvent(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        java.time.Instant.now()
                )
        );

        return user;
    }
}
