package com.custard.ehr.identity.application.service;

import com.custard.ehr.identity.application.dto.AuthResponse;
import com.custard.ehr.identity.application.dto.LoginRequest;
import com.custard.ehr.identity.application.ports.UserRepository;
import com.custard.ehr.identity.domain.User;
import com.custard.ehr.identity.infrastructure.config.JwtService;
import com.custard.ehr.shared.events.UserLoggedInEvent;
import com.custard.ehr.shared.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ApplicationEventPublisher eventPublisher;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            ApplicationEventPublisher eventPublisher
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        if (!user.isActive()) {
            logger.info("User {} has been disabled", user.getId());
            throw new UnauthorizedException("User account is disabled");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        String token = jwtService.generateToken(user);

        // 🔥 publish login event
        eventPublisher.publishEvent(
                new UserLoggedInEvent(
                        user.getId(),
                        user.getUsername(),
                        java.time.Instant.now()
                ));
        logger.info("User {} logged in", user.getId());
        return new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                token,
                "Bearer",
                "Login successful"
        );
    }
}