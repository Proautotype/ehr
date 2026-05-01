package com.custard.ehr.patient.domain;

import com.custard.ehr.shared.domain.AuditableEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "patient_chronic_conditions")
public class ChronicCondition extends AuditableEntity {

    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String notes;

    protected ChronicCondition() {
    }

    public ChronicCondition(Patient patient, String name, String notes) {
        this.patient = patient;
        this.name = name;
        this.notes = notes;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNotes() {
        return notes;
    }
}