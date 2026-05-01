package com.custard.ehr.identity.presentation;

import com.custard.ehr.identity.application.dto.AuthResponse;
import com.custard.ehr.identity.application.dto.LoginRequest;
import com.custard.ehr.identity.application.dto.RegisterUserRequest;
import com.custard.ehr.identity.application.service.AuthService;
import com.custard.ehr.identity.application.service.UserService;
import com.custard.ehr.identity.domain.User;
import com.custard.ehr.shared.infrastruture.web.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(
            AuthService authService,
            UserService userService
    ) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(
            @Valid @RequestBody RegisterUserRequest request
    ) {
        User user = userService.register(request);

        AuthResponse response = new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                null,
                null,
                "User registered successfully"
        );

        return ApiResponse.success(response);
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ApiResponse.success(authService.login(request));
    }
}