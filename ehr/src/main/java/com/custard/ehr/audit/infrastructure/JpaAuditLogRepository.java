package com.custard.ehr.audit.infrastructure;

import com.custard.ehr.audit.application.ports.AuditLogRepository;
import com.custard.ehr.audit.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaAuditLogRepository
        extends JpaRepository<AuditLog, UUID>, AuditLogRepository {

    List<AuditLog> findTop100ByOrderByOccurredAtDesc();

    List<AuditLog> findByActorIdOrderByOccurredAtDesc(UUID actorId);

    List<AuditLog> findByEntityTypeAndEntityIdOrderByOccurredAtDesc(
            String entityType,
            UUID entityId
    );
}