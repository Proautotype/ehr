package com.custard.ehr.audit.presentation;

import com.custard.ehr.audit.application.AuditService;
import com.custard.ehr.audit.domain.AuditLog;
import com.custard.ehr.shared.infrastruture.web.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit-logs")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    public com.custard.ehr.shared.infrastruture.web.ApiResponse<List<AuditLog>> recentLogs() {
        return ApiResponse.success(auditService.recentLogs());
    }

    @GetMapping("/actor/{actorId}")
    public ApiResponse<List<AuditLog>> logsByActor(
            @PathVariable UUID actorId
    ) {
        return ApiResponse.success(auditService.logsByActor(actorId));
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public ApiResponse<List<AuditLog>> logsByEntity(
            @PathVariable String entityType,
            @PathVariable UUID entityId
    ) {
        return ApiResponse.success(
                auditService.logsByEntity(entityType, entityId)
        );
    }
}