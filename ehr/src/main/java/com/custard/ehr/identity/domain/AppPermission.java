package com.custard.ehr.identity.domain;

import com.custard.ehr.shared.domain.AuditableEntity;
import com.custard.ehr.shared.security.Permission;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
        name = "app_permissions",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_app_permissions_name", columnNames = "name")
        },
        indexes = {
                @Index(name = "idx_app_permissions_name", columnList = "name")
        }
)
public class AppPermission extends AuditableEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 100)
    private Permission name;

    @Column(length = 500)
    private String description;

    protected AppPermission() {
        // for JPA
    }

    public AppPermission(Permission name) {
        this.name = Objects.requireNonNull(name, "Permission name is required");
    }

    public AppPermission(Permission name, String description) {
        this.name = Objects.requireNonNull(name, "Permission name is required");
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public Permission getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    // equality based on business key (name)

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppPermission that)) return false;
        return name == that.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}