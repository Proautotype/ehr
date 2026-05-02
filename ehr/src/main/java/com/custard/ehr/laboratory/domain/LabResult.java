package com.custard.ehr.laboratory.domain;

import com.custard.ehr.shared.domain.AuditableEntity;
import com.custard.ehr.shared.exception.BusinessException;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "lab_results",
        indexes = {
                @Index(name = "idx_lab_result_recorded_at", columnList = "recordedAt"),
                @Index(name = "idx_lab_result_recorded_by", columnList = "recordedBy")
        }
)
public class LabResult extends AuditableEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_order_item_id", nullable = false, unique = true)
    private LabOrderItem labOrderItem;

    @Column(nullable = false, length = 2000)
    private String resultValue;

    private String unit;

    private String referenceRange;

    @Column(length = 2000)
    private String interpretation;

    @Column(nullable = false)
    private UUID recordedBy;

    @Column(nullable = false)
    private Instant recordedAt;

    protected LabResult() {
    }

    public LabResult(
            LabOrderItem labOrderItem,
            String resultValue,
            String unit,
            String referenceRange,
            String interpretation,
            UUID recordedBy
    ) {
        if (resultValue == null || resultValue.isBlank()) {
            throw new BusinessException("Lab result value is required");
        }

        this.labOrderItem = labOrderItem;
        this.resultValue = resultValue;
        this.unit = unit;
        this.referenceRange = referenceRange;
        this.interpretation = interpretation;
        this.recordedBy = recordedBy;
        this.recordedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getResultValue() {
        return resultValue;
    }

    public String getUnit() {
        return unit;
    }

    public String getReferenceRange() {
        return referenceRange;
    }

    public String getInterpretation() {
        return interpretation;
    }

    public UUID getRecordedBy() {
        return recordedBy;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }
}