package com.custard.ehr.prescription.domain;

import com.custard.ehr.shared.domain.AuditableEntity;
import com.custard.ehr.shared.exception.BusinessException;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.*;

@Entity
@Table(
        name = "prescriptions",
        indexes = {
                @Index(name = "idx_prescription_encounter", columnList = "encounterId"),
                @Index(name = "idx_prescription_patient", columnList = "patientId"),
                @Index(name = "idx_prescription_status", columnList = "status"),
                @Index(name = "idx_prescription_prescribed_at", columnList = "prescribedAt")
        }
)
public class Prescription extends AuditableEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @Column(nullable = false)
    private UUID encounterId;

    @Column(nullable = false)
    private UUID patientId;

    @Column(nullable = false)
    private UUID prescribedBy;

    @Column(nullable = false)
    private Instant prescribedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrescriptionStatus status = PrescriptionStatus.CREATED;

    @Column(length = 2000)
    private String notes;

    @Version
    private Long version;

    @OneToMany(
            mappedBy = "prescription",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PrescriptionItem> items = new ArrayList<>();

    protected Prescription() {
    }

    public Prescription(
            UUID encounterId,
            UUID patientId,
            UUID prescribedBy,
            String notes
    ) {
        this.encounterId = Objects.requireNonNull(encounterId, "Encounter ID is required");
        this.patientId = Objects.requireNonNull(patientId, "Patient ID is required");
        this.prescribedBy = Objects.requireNonNull(prescribedBy, "Prescriber ID is required");
        this.notes = notes;
        this.prescribedAt = Instant.now();
    }

    public void addItem(
            UUID drugId,
            String drugName,
            String strength,
            String form,
            String dosage,
            String frequency,
            String duration,
            String route,
            Integer quantity,
            String instructions
    ) {
        ensureEditable();

        PrescriptionItem item = new PrescriptionItem(
                this,
                drugId,
                drugName,
                strength,
                form,
                dosage,
                frequency,
                duration,
                route,
                quantity,
                instructions
        );

        this.items.add(item);
    }

    public void sendToPharmacy() {
        ensureEditable();

        if (items.isEmpty()) {
            throw new BusinessException("Prescription must contain at least one item before sending to pharmacy");
        }

        this.status = PrescriptionStatus.SENT_TO_PHARMACY;
    }

    public void markDispensed() {
        if (this.status != PrescriptionStatus.SENT_TO_PHARMACY) {
            throw new BusinessException("Only prescriptions sent to pharmacy can be marked as dispensed");
        }

        this.status = PrescriptionStatus.DISPENSED;
    }

    public void markPartiallyDispensed() {
        if (this.status != PrescriptionStatus.SENT_TO_PHARMACY) {
            throw new BusinessException("Only prescriptions sent to pharmacy can be partially dispensed");
        }

        this.status = PrescriptionStatus.PARTIALLY_DISPENSED;
    }

    public void cancel() {
        if (this.status == PrescriptionStatus.DISPENSED) {
            throw new BusinessException("Dispensed prescription cannot be cancelled");
        }

        this.status = PrescriptionStatus.CANCELLED;
    }

    private void ensureEditable() {
        if (this.status != PrescriptionStatus.CREATED) {
            throw new BusinessException("Prescription can only be edited while in CREATED status");
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getEncounterId() {
        return encounterId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public UUID getPrescribedBy() {
        return prescribedBy;
    }

    public Instant getPrescribedAt() {
        return prescribedAt;
    }

    public PrescriptionStatus getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public List<PrescriptionItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}