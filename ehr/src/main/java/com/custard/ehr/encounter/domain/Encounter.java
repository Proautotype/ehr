package com.custard.ehr.encounter.domain;

import com.custard.ehr.shared.domain.AuditableEntity;
import com.custard.ehr.shared.exception.BusinessException;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "encounters",
        indexes = {
                @Index(name = "idx_encounter_patient", columnList = "patientId"),
                @Index(name = "idx_encounter_status", columnList = "status"),
                @Index(name = "idx_encounter_opened_at", columnList = "openedAt")
        }
)
@Getter
public class Encounter extends AuditableEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @Column(nullable = false)
    private UUID patientId;

    @Column(nullable = false)
    private UUID openedBy;

    private UUID completedBy;

    @Column(nullable = false)
    private Instant openedAt;

    private Instant completedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EncounterStatus status = EncounterStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LabStatus labStatus = LabStatus.NOT_REQUIRED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PharmacyStatus pharmacyStatus = PharmacyStatus.NOT_REQUIRED;

    @Column(length = 1000)
    private String reasonForVisit;

    @Version
    private Long version;

    protected Encounter() {
    }

    public Encounter(UUID patientId, UUID openedBy, String reasonForVisit) {
        this.patientId = patientId;
        this.openedBy = openedBy;
        this.reasonForVisit = reasonForVisit;
        this.openedAt = Instant.now();
    }

    public void markLabPending() {
        this.labStatus = LabStatus.PENDING;
    }

    public void markLabCompleted() {
        this.labStatus = LabStatus.COMPLETED;
    }

    public void markPharmacyPending() {
        this.pharmacyStatus = PharmacyStatus.PENDING;
    }

    public void markPharmacyDispensed() {
        this.pharmacyStatus = PharmacyStatus.DISPENSED;
    }

    public void markPaymentPaid() {
        this.paymentStatus = PaymentStatus.PAID;
    }

    public void waivePayment() {
        this.paymentStatus = PaymentStatus.WAIVED;
    }

    public void complete(UUID completedBy) {
        if (this.status != EncounterStatus.ACTIVE) {
            throw new BusinessException("Only active encounters can be completed");
        }

        if (this.paymentStatus == PaymentStatus.PENDING) {
            throw new BusinessException("Encounter cannot be completed while payment is pending");
        }

        if (this.labStatus == LabStatus.PENDING) {
            throw new BusinessException("Encounter cannot be completed while lab is pending");
        }

        if (this.pharmacyStatus == PharmacyStatus.PENDING) {
            throw new BusinessException("Encounter cannot be completed while pharmacy is pending");
        }

        this.status = EncounterStatus.COMPLETED;
        this.completedBy = completedBy;
        this.completedAt = Instant.now();
    }

    public void cancel() {
        if (this.status == EncounterStatus.COMPLETED) {
            throw new BusinessException("Completed encounter cannot be cancelled");
        }

        this.status = EncounterStatus.CANCELLED;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public UUID getOpenedBy() {
        return openedBy;
    }

    public UUID getCompletedBy() {
        return completedBy;
    }

    public Instant getOpenedAt() {
        return openedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public EncounterStatus getStatus() {
        return status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public LabStatus getLabStatus() {
        return labStatus;
    }

    public PharmacyStatus getPharmacyStatus() {
        return pharmacyStatus;
    }

    public String getReasonForVisit() {
        return reasonForVisit;
    }
}