package com.custard.ehr.audit.application.ports;

import com.custard.ehr.audit.domain.AuditLog;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository {

    AuditLog save(AuditLog auditLog);

    List<AuditLog> findTop100ByOrderByOccurredAtDesc();

    List<AuditLog> findByActorIdOrderByOccurredAtDesc(UUID actorId);

    List<AuditLog> findByEntityTypeAndEntityIdOrderByOccurredAtDesc(
            String entityType,
            UUID entityId
    );
}