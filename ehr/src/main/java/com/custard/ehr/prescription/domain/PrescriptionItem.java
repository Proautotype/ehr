package com.custard.ehr.prescription.domain;

import com.custard.ehr.shared.domain.AuditableEntity;
import com.custard.ehr.shared.exception.BusinessException;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(
        name = "prescription_items",
        indexes = {
                @Index(name = "idx_prescription_item_prescription", columnList = "prescription_id"),
                @Index(name = "idx_prescription_item_drug", columnList = "drugId")
        }
)
public class PrescriptionItem extends AuditableEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @Column(nullable = false)
    private UUID drugId;

    @Column(nullable = false)
    private String drugName;

    private String strength;

    private String form;

    @Column(nullable = false)
    private String dosage;

    @Column(nullable = false)
    private String frequency;

    @Column(nullable = false)
    private String duration;

    private String route;

    private Integer quantity;

    @Column(length = 1000)
    private String instructions;

    protected PrescriptionItem() {
    }

    PrescriptionItem(
            Prescription prescription,
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
        validate(dosage, frequency, duration, quantity);

        this.prescription = prescription;
        this.drugId = drugId;
        this.drugName = drugName;
        this.strength = strength;
        this.form = form;
        this.dosage = dosage;
        this.frequency = frequency;
        this.duration = duration;
        this.route = route;
        this.quantity = quantity;
        this.instructions = instructions;
    }

    private void validate(
            String dosage,
            String frequency,
            String duration,
            Integer quantity
    ) {
        if (isBlank(dosage)) {
            throw new BusinessException("Dosage is required");
        }

        if (isBlank(frequency)) {
            throw new BusinessException("Frequency is required");
        }

        if (isBlank(duration)) {
            throw new BusinessException("Duration is required");
        }

        if (quantity != null && quantity <= 0) {
            throw new BusinessException("Quantity must be greater than zero");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public UUID getId() {
        return id;
    }

    public UUID getDrugId() {
        return drugId;
    }

    public String getDrugName() {
        return drugName;
    }

    public String getStrength() {
        return strength;
    }

    public String getForm() {
        return form;
    }

    public String getDosage() {
        return dosage;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getDuration() {
        return duration;
    }

    public String getRoute() {
        return route;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getInstructions() {
        return instructions;
    }
}