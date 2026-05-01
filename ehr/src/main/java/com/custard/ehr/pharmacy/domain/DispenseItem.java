package com.custard.ehr.pharmacy.domain;

import com.custard.ehr.shared.domain.AuditableEntity;
import com.custard.ehr.shared.exception.BusinessException;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(
        name = "dispense_items",
        indexes = {
                @Index(name = "idx_dispense_item_record", columnList = "dispense_record_id"),
                @Index(name = "idx_dispense_item_drug", columnList = "drugId"),
                @Index(name = "idx_dispense_item_prescription_item", columnList = "prescriptionItemId")
        }
)
public class DispenseItem extends AuditableEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "dispense_record_id", nullable = false)
    private DispenseRecord dispenseRecord;

    @Column(nullable = false)
    private UUID prescriptionItemId;

    @Column(nullable = false)
    private UUID drugId;

    @Column(nullable = false)
    private String drugName;

    @Column(nullable = false)
    private Integer prescribedQuantity;

    @Column(nullable = false)
    private Integer dispensedQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DispenseItemStatus status;

    @Enumerated(EnumType.STRING)
    private NonDispenseReason reason;

    @Column(length = 1000)
    private String note;

    protected DispenseItem() {
    }

    public DispenseItem(
            DispenseRecord dispenseRecord,
            UUID prescriptionItemId,
            UUID drugId,
            String drugName,
            Integer prescribedQuantity,
            Integer dispensedQuantity,
            NonDispenseReason reason,
            String note
    ) {
        validate(prescribedQuantity, dispensedQuantity, reason);

        this.dispenseRecord = dispenseRecord;
        this.prescriptionItemId = prescriptionItemId;
        this.drugId = drugId;
        this.drugName = drugName;
        this.prescribedQuantity = prescribedQuantity;
        this.dispensedQuantity = dispensedQuantity;
        this.status = determineStatus(prescribedQuantity, dispensedQuantity);
        this.reason = reason;
        this.note = note;
    }

    private void validate(
            Integer prescribedQuantity,
            Integer dispensedQuantity,
            NonDispenseReason reason
    ) {
        if (prescribedQuantity == null || prescribedQuantity <= 0) {
            throw new BusinessException("Prescribed quantity must be greater than zero");
        }

        if (dispensedQuantity == null || dispensedQuantity < 0) {
            throw new BusinessException("Dispensed quantity cannot be negative");
        }

        if (dispensedQuantity > prescribedQuantity) {
            throw new BusinessException("Dispensed quantity cannot exceed prescribed quantity");
        }

        if (dispensedQuantity < prescribedQuantity && reason == null) {
            throw new BusinessException("Reason is required when item is not fully dispensed");
        }
    }

    private DispenseItemStatus determineStatus(Integer prescribedQuantity, Integer dispensedQuantity) {
        if (dispensedQuantity == 0) {
            return DispenseItemStatus.NOT_DISPENSED;
        }

        if (dispensedQuantity < prescribedQuantity) {
            return DispenseItemStatus.PARTIALLY_DISPENSED;
        }

        return DispenseItemStatus.DISPENSED;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPrescriptionItemId() {
        return prescriptionItemId;
    }

    public UUID getDrugId() {
        return drugId;
    }

    public String getDrugName() {
        return drugName;
    }

    public Integer getPrescribedQuantity() {
        return prescribedQuantity;
    }

    public Integer getDispensedQuantity() {
        return dispensedQuantity;
    }

    public DispenseItemStatus getStatus() {
        return status;
    }

    public NonDispenseReason getReason() {
        return reason;
    }

    public String getNote() {
        return note;
    }
}