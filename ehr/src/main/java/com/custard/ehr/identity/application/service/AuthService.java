package com.custard.ehr.identity.application.service;

import com.custard.ehr.identity.application.dto.AuthResponse;
import com.custard.ehr.identity.application.dto.LoginRequest;
import com.custard.ehr.identity.application.dto.RefreshTokenRequest;
import com.custard.ehr.identity.application.ports.PasswordHasher;
import com.custard.ehr.identity.application.ports.UserRepository;
import com.custard.ehr.identity.domain.User;
import com.custard.ehr.identity.infrastructure.JwtService;
import com.custard.ehr.shared.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

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
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        if (!user.isActive()) {
            throw new UnauthorizedException("User account is disabled");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid username or password");
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

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (!user.isActive()) {
            throw new UnauthorizedException("User account is disabled");
        }

        return buildAuthResponse(user, "Token refreshed successfully");
    }

    private AuthResponse buildAuthResponse(User user, String message) {
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