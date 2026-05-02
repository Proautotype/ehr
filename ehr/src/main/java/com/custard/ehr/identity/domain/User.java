package com.custard.ehr.identity.domain;

import com.custard.ehr.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "app_users")
public class User extends AuditableEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private final UUID id = UUID.randomUUID();

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<UserRole> roles = new HashSet<>();

    protected User() {
    }

    public User(String fullName, String username, String email, String passwordHash) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean isActive() {
        return active;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public void assignRole(AppRole role) {
        boolean alreadyAssigned = this.roles.stream()
                .anyMatch(userRole -> userRole.getRole().getName() == role.getName());

        if (!alreadyAssigned) {
            this.roles.add(new UserRole(this, role));
        }
    }

    public void resetPassword(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void clearRoles() {
        this.roles.clear();
    }
}