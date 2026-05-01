package com.custard.ehr.patient.domain;

import com.custard.ehr.shared.domain.AuditableEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "patient_allergies")
public class Allergy extends AuditableEntity {

    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(nullable = false)
    private String name;

    private String severity;

    private String reaction;

    protected Allergy() {
    }

    public Allergy(Patient patient, String name, String severity, String reaction) {
        this.patient = patient;
        this.name = name;
        this.severity = severity;
        this.reaction = reaction;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSeverity() {
        return severity;
    }

    public String getReaction() {
        return reaction;
    }
}