package com.custard.ehr.laboratory.domain;

import com.custard.ehr.shared.domain.AuditableEntity;
import com.custard.ehr.shared.exception.BusinessException;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.*;

@Entity
@Table(
        name = "lab_orders",
        indexes = {
                @Index(name = "idx_lab_order_encounter", columnList = "encounterId"),
                @Index(name = "idx_lab_order_patient", columnList = "patientId"),
                @Index(name = "idx_lab_order_status", columnList = "status"),
                @Index(name = "idx_lab_order_ordered_at", columnList = "orderedAt")
        }
)
public class LabOrder extends AuditableEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @Column(nullable = false)
    private UUID encounterId;

    @Column(nullable = false)
    private UUID patientId;

    @Column(nullable = false)
    private UUID orderedBy;

    @Column(nullable = false)
    private Instant orderedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LabOrderStatus status = LabOrderStatus.ORDERED;

    @Column(length = 1000)
    private String clinicalNote;

    @Version
    private Long version;

    @OneToMany(mappedBy = "labOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LabOrderItem> items = new ArrayList<>();

    protected LabOrder() {
    }

    public LabOrder(UUID encounterId, UUID patientId, UUID orderedBy, String clinicalNote) {
        this.encounterId = Objects.requireNonNull(encounterId, "Encounter ID is required");
        this.patientId = Objects.requireNonNull(patientId, "Patient ID is required");
        this.orderedBy = Objects.requireNonNull(orderedBy, "Ordered by is required");
        this.clinicalNote = clinicalNote;
        this.orderedAt = Instant.now();
    }

    public void addItem(UUID labTestId, String testName, String testCode) {
        ensureEditable();

        boolean duplicate = items.stream()
                .anyMatch(item -> item.getLabTestId().equals(labTestId));

        if (duplicate) {
            throw new BusinessException("Duplicate lab test in order: " + testName);
        }

        items.add(new LabOrderItem(this, labTestId, testName, testCode));
    }

    public void markResulted() {
        long resultedCount = items.stream()
                .filter(LabOrderItem::hasResult)
                .count();

        if (resultedCount == 0) {
            this.status = LabOrderStatus.ORDERED;
        } else if (resultedCount < items.size()) {
            this.status = LabOrderStatus.PARTIALLY_RESULTED;
        } else {
            this.status = LabOrderStatus.COMPLETED;
        }
    }

    public void cancel() {
        if (this.status == LabOrderStatus.COMPLETED) {
            throw new BusinessException("Completed lab order cannot be cancelled");
        }

        this.status = LabOrderStatus.CANCELLED;
    }

    private void ensureEditable() {
        if (this.status != LabOrderStatus.ORDERED) {
            throw new BusinessException("Lab order can only be edited while ORDERED");
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

    public UUID getOrderedBy() {
        return orderedBy;
    }

    public Instant getOrderedAt() {
        return orderedAt;
    }

    public LabOrderStatus getStatus() {
        return status;
    }

    public String getClinicalNote() {
        return clinicalNote;
    }

    public List<LabOrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}