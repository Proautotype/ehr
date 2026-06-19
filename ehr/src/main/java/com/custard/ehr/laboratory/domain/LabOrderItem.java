package com.custard.ehr.laboratory.domain;

import com.custard.ehr.shared.domain.AuditableEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(
        name = "lab_order_items",
        indexes = {
                @Index(name = "idx_lab_order_item_order", columnList = "lab_order_id"),
                @Index(name = "idx_lab_order_item_test", columnList = "labTestId")
        }
)
public class LabOrderItem extends AuditableEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_order_id", nullable = false)
    private LabOrder labOrder;

    @Column(nullable = false)
    private UUID labTestId;

    @Column(nullable = false)
    private String testName;

    private String testCode;

    @OneToOne(mappedBy = "labOrderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private LabResult result;

    protected LabOrderItem() {
    }

    LabOrderItem(LabOrder labOrder, UUID labTestId, String testName, String testCode) {
        this.labOrder = labOrder;
        this.labTestId = labTestId;
        this.testName = testName;
        this.testCode = testCode;
    }

    public void recordResult(
            String resultValue,
            String referenceRange,
            String interpretation,
            UUID recordedBy
    ) {
        this.result = new LabResult(
                this,
                resultValue,
                referenceRange,
                interpretation,
                recordedBy
        );
    }

    public boolean hasResult() {
        return result != null;
    }

    public UUID getId() {
        return id;
    }

    public UUID getLabTestId() {
        return labTestId;
    }

    public String getTestName() {
        return testName;
    }

    public String getTestCode() {
        return testCode;
    }

    public LabResult getResult() {
        return result;
    }
}