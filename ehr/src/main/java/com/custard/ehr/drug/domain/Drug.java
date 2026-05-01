package com.custard.ehr.drug.domain;

import com.custard.ehr.shared.domain.AuditableEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(
        name = "drugs",
        indexes = {
                @Index(name = "idx_drug_name", columnList = "name")
        }
)
public class Drug extends AuditableEntity {

    @Id
    private UUID id = UUID.randomUUID();

    @Column(nullable = false, unique = true)
    private String name;

    private String strength;

    private String form; // tablet, syrup, injection

    private boolean active = true;

    protected Drug() {}

    public Drug(String name, String strength, String form) {
        this.name = name;
        this.strength = strength;
        this.form = form;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStrength() {
        return strength;
    }

    public String getForm() {
        return form;
    }

    public boolean isActive() {
        return active;
    }
}