package com.custard.ehr.audit.domain;

import com.custard.ehr.shared.domain.AuditableEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
public class AuditLog extends AuditableEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String module;

    @Column(nullable = false)
    private String entityType;

    private UUID entityId;

    private UUID actorId;

    private String actorUsername;

    @Column(nullable = false)
    private Instant occurredAt;

    @Column(length = 2000)
    private String description;

    protected AuditLog() {
    }

    public AuditLog(
            String action,
            String module,
            String entityType,
            UUID entityId,
            UUID actorId,
            String actorUsername,
            Instant occurredAt,
            String description
    ) {
        this.action = action;
        this.module = module;
        this.entityType = entityType;
        this.entityId = entityId;
        this.actorId = actorId;
        this.actorUsername = actorUsername;
        this.occurredAt = occurredAt;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public String getModule() {
        return module;
    }

    public String getEntityType() {
        return entityType;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public UUID getActorId() {
        return actorId;
    }

    public String getActorUsername() {
        return actorUsername;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getDescription() {
        return description;
    }
}