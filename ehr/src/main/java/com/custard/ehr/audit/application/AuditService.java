package com.custard.ehr.audit.application;

import com.custard.ehr.audit.application.ports.AuditLogRepository;
import com.custard.ehr.audit.domain.AuditLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public AuditLog record(
            String action,
            String module,
            String entityType,
            UUID entityId,
            UUID actorId,
            String actorUsername,
            Instant occurredAt,
            String description
    ) {
        AuditLog auditLog = new AuditLog(
                action,
                module,
                entityType,
                entityId,
                actorId,
                actorUsername,
                occurredAt,
                description
        );

        return auditLogRepository.save(auditLog);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> recentLogs() {
        return auditLogRepository.findTop100ByOrderByOccurredAtDesc();
    }

    @Transactional(readOnly = true)
    public List<AuditLog> logsByActor(UUID actorId) {
        return auditLogRepository.findByActorIdOrderByOccurredAtDesc(actorId);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> logsByEntity(String entityType, UUID entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByOccurredAtDesc(
                entityType,
                entityId
        );
    }
}