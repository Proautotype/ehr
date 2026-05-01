package com.custard.ehr.pharmacy.domain;

import com.custard.ehr.shared.domain.AuditableEntity;
import com.custard.ehr.shared.exception.BusinessException;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "dispense_records",
        indexes = {
                @Index(name = "idx_dispense_prescription", columnList = "prescriptionId"),
                @Index(name = "idx_dispense_patient", columnList = "patientId"),
                @Index(name = "idx_dispense_dispensed_at", columnList = "dispensedAt")
        }
)
public class DispenseRecord extends AuditableEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @Column(nullable = false)
    private UUID prescriptionId;

    @Column(nullable = false)
    private UUID encounterId;

    @Column(nullable = false)
    private UUID patientId;

    @Column(nullable = false)
    private UUID dispensedBy;

    @Column(nullable = false)
    private Instant dispensedAt;

    @Column(nullable = false)
    private boolean partial;

    @OneToMany(mappedBy = "dispenseRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DispenseItem> items = new ArrayList<>();

    protected DispenseRecord() {
    }

    public DispenseRecord(
            UUID prescriptionId,
            UUID encounterId,
            UUID patientId,
            UUID dispensedBy
    ) {
        this.prescriptionId = prescriptionId;
        this.encounterId = encounterId;
        this.patientId = patientId;
        this.dispensedBy = dispensedBy;
        this.dispensedAt = Instant.now();
    }

    public void addItem(
            UUID prescriptionItemId,
            UUID drugId,
            String drugName,
            Integer prescribedQuantity,
            Integer dispensedQuantity,
            NonDispenseReason reason,
            String note
    ) {
        DispenseItem item = new DispenseItem(
                this,
                prescriptionItemId,
                drugId,
                drugName,
                prescribedQuantity,
                dispensedQuantity,
                reason,
                note
        );

        this.items.add(item);
        recalculatePartialStatus();
    }

    private void recalculatePartialStatus() {
        if (items.isEmpty()) {
            this.partial = false;
            return;
        }

        this.partial = items.stream()
                .anyMatch(item -> item.getStatus() != DispenseItemStatus.DISPENSED);
    }

    public void validateHasItems() {
        if (items.isEmpty()) {
            throw new BusinessException("Dispense record must contain at least one item");
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getPrescriptionId() {
        return prescriptionId;
    }

    public UUID getEncounterId() {
        return encounterId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public UUID getDispensedBy() {
        return dispensedBy;
    }

    public Instant getDispensedAt() {
        return dispensedAt;
    }

    public boolean isPartial() {
        return partial;
    }

    public List<DispenseItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}