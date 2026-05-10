package com.custard.ehr.identity.domain;

import com.custard.ehr.shared.domain.AuditableEntity;
import com.custard.ehr.shared.security.Role;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "app_roles")
public class AppRole extends AuditableEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private final UUID id = UUID.randomUUID();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private Role name;

    @Column(length = 500)
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "app_role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"),
            uniqueConstraints = {
                    @UniqueConstraint(
                            name = "uq_app_role_permissions_role_permission",
                            columnNames = {"role_id", "permission_id"}
                    )
            }
    )
    private Set<AppPermission> permissions = new HashSet<>();

    protected AppRole() {
    }

    public AppRole(Role name, String description) {
        this.name = name;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public Role getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}