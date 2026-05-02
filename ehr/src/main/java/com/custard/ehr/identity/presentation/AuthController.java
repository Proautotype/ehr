package com.custard.ehr.identity.presentation;

import com.custard.ehr.identity.application.dto.AuthResponse;
import com.custard.ehr.identity.application.dto.LoginRequest;
import com.custard.ehr.identity.application.dto.RefreshTokenRequest;
import com.custard.ehr.identity.application.service.AuthService;
import com.custard.ehr.shared.infrastruture.web.AppApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication and token management APIs")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login",
            description = "Authenticate user credentials and return access + refresh tokens"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public AppApiResponse<AuthResponse> login(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User login payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
            )
            @RequestBody LoginRequest request
    ) {
        return AppApiResponse.success(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh Token",
            description = "Generate a new access token using a valid refresh token"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Token refreshed",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    public AppApiResponse<AuthResponse> refresh(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Refresh token payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RefreshTokenRequest.class))
            )
            @RequestBody RefreshTokenRequest request
    ) {
        return AppApiResponse.success(authService.refreshToken(request));
    }
}