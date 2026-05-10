package com.custard.ehr.identity.application.service;

import com.custard.ehr.identity.application.dto.AuthResponse;
import com.custard.ehr.identity.application.dto.LoginRequest;
import com.custard.ehr.identity.application.dto.RefreshTokenRequest;
import com.custard.ehr.identity.application.ports.UserRepository;
import com.custard.ehr.identity.domain.AppUser;
import com.custard.ehr.identity.infrastructure.config.JwtService;
import com.custard.ehr.shared.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final Logger log = LoggerFactory.getLogger(AuthService.class);

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        AppUser user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password on find"));

        log.info("user found {} ", user.getUsername());

        if (!user.isActive()) {
            throw new UnauthorizedException("User account is disabled");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid username or password!!");
        }

        return buildAuthResponse(user, "Login successful");
    }

    @Transactional(readOnly = true)
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        String username = jwtService.extractUsername(refreshToken);

        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (!user.isActive()) {
            throw new UnauthorizedException("User account is disabled");
        }

        return buildAuthResponse(user, "Token refreshed successfully");
    }

    private AuthResponse buildAuthResponse(AppUser user, String message) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                accessToken,
                refreshToken,
                "Bearer",
                message
        );
    }
}