package com.custard.ehr.audit.presentation;

import com.custard.ehr.audit.application.AuditService;
import com.custard.ehr.audit.domain.AuditLog;
import com.custard.ehr.shared.infrastruture.web.AppApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit-logs")
@Tag(name = "Audit Logs", description = "Audit trail and system activity logs")
@SecurityRequirement(name = "bearerAuth")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    @Operation(
            summary = "Get recent audit logs",
            description = "Returns a list of the most recent system audit logs"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Audit logs retrieved successfully",
                    content = @Content(schema = @Schema(implementation = AuditLog.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<List<AuditLog>> recentLogs() {
        return AppApiResponse.success(auditService.recentLogs());
    }

    @GetMapping("/actor/{actorId}")
    @Operation(
            summary = "Get audit logs by actor",
            description = "Returns audit logs associated with a specific actor (user/system)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Audit logs retrieved successfully",
                    content = @Content(schema = @Schema(implementation = AuditLog.class))
            ),
            @ApiResponse(responseCode = "404", description = "Actor not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<List<AuditLog>> logsByActor(
            @PathVariable UUID actorId
    ) {
        return AppApiResponse.success(auditService.logsByActor(actorId));
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    @Operation(
            summary = "Get audit logs by entity",
            description = "Returns audit logs related to a specific entity type and entity ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Audit logs retrieved successfully",
                    content = @Content(schema = @Schema(implementation = AuditLog.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid entity type or ID"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<List<AuditLog>> logsByEntity(
            @PathVariable String entityType,
            @PathVariable UUID entityId
    ) {
        return AppApiResponse.success(
                auditService.logsByEntity(entityType, entityId)
        );
    }
}