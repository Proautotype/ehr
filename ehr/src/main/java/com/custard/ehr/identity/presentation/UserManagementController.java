package com.custard.ehr.identity.presentation;

import com.custard.ehr.identity.application.dto.AssignRoleRequest;
import com.custard.ehr.identity.application.dto.CreateStaffUserRequest;
import com.custard.ehr.identity.application.dto.ResetPasswordRequest;
import com.custard.ehr.identity.application.dto.UserResponse;
import com.custard.ehr.identity.application.service.UserManagementService;
import com.custard.ehr.shared.infrastruture.web.AppApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
@Tag(name = "User Management", description = "User administration and management APIs")
@SecurityRequirement(name = "bearerAuth")
public class UserManagementController {

    private final UserManagementService userManagementService;

    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @PreAuthorize("hasAuthority('USER_MANAGE')")
    @PostMapping
    @Operation(
            summary = "Create staff user",
            description = "Creates a new staff user with assigned roles and attributes"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Staff user created successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<UserResponse> createStaffUser(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Staff user creation payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateStaffUserRequest.class))
            )
            @RequestBody CreateStaffUserRequest request
    ) {
        return AppApiResponse.success(
                "Staff user created successfully",
                userManagementService.createStaffUser(request)
        );
    }

    @PreAuthorize("hasAuthority('USER_VIEW')")
    @GetMapping("/{id}")
    @Operation(
            summary = "Get user by ID",
            description = "Fetch a single user by their unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<UserResponse> getById(@PathVariable UUID id) {
        return AppApiResponse.success(userManagementService.getById(id));
    }

    @PreAuthorize("hasAuthority('USER_VIEW')")
    @GetMapping
    @Operation(
            summary = "Get recent users",
            description = "Returns a list of recently created or active users"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Users retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<List<UserResponse>> recentUsers() {
        return AppApiResponse.success(userManagementService.recentUsers());
    }

    @PreAuthorize("hasAuthority('USER_VIEW')")
    @GetMapping("/search")
    @Operation(
            summary = "Search users",
            description = "Search users by query string (name, email, etc.)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Search results returned",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid query"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<List<UserResponse>> search(@RequestParam String query) {
        return AppApiResponse.success(userManagementService.search(query));
    }

    @PreAuthorize("hasAuthority('USER_MANAGE')")
    @PatchMapping("/{id}/roles")
    @Operation(
            summary = "Assign role to user",
            description = "Assigns or updates roles for a specific user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Role assigned successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<UserResponse> assignRole(
            @PathVariable UUID id,
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Role assignment payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AssignRoleRequest.class))
            )
            @RequestBody AssignRoleRequest request
    ) {
        return AppApiResponse.success(
                "Role assigned successfully",
                userManagementService.assignRole(id, request)
        );
    }

    @PreAuthorize("hasAuthority('USER_MANAGE')")
    @PatchMapping("/{id}/activate")
    @Operation(
            summary = "Activate user",
            description = "Activates a user account"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User activated successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<UserResponse> activate(@PathVariable UUID id) {
        return AppApiResponse.success(
                "User activated successfully",
                userManagementService.activate(id)
        );
    }

    @PreAuthorize("hasAuthority('USER_MANAGE')")
    @PatchMapping("/{id}/deactivate")
    @Operation(
            summary = "Deactivate user",
            description = "Deactivates a user account"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User deactivated successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<UserResponse> deactivate(@PathVariable UUID id) {
        return AppApiResponse.success(
                "User deactivated successfully",
                userManagementService.deactivate(id)
        );
    }

    @PreAuthorize("hasAuthority('USER_MANAGE')")
    @PatchMapping("/{id}/reset-password")
    @Operation(
            summary = "Reset user password",
            description = "Resets the password for a specific user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password reset successfully"
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<Void> resetPassword(
            @PathVariable UUID id,
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Password reset payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ResetPasswordRequest.class))
            )
            @RequestBody ResetPasswordRequest request
    ) {
        userManagementService.resetPassword(id, request);
        return AppApiResponse.success("Password reset successfully", null);
    }
}