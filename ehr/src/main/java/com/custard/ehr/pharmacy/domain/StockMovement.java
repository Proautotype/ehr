package com.custard.ehr.pharmacy.domain;

import com.custard.ehr.shared.domain.AuditableEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "stock_movements",
        indexes = {
                @Index(name = "idx_stock_movement_drug", columnList = "drugId"),
                @Index(name = "idx_stock_movement_type", columnList = "movementType"),
                @Index(name = "idx_stock_movement_created_at", columnList = "occurredAt")
        }
)
public class StockMovement extends AuditableEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @Column(nullable = false)
    private UUID drugId;

    @Column(nullable = false)
    private String drugName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockMovementType movementType;

    @Column(nullable = false)
    private Integer quantity;

    private UUID referenceId;

    @Column(nullable = false)
    private UUID performedBy;

    @Column(nullable = false)
    private Instant occurredAt;

    @Column(length = 1000)
    private String note;

    protected StockMovement() {
    }

    public StockMovement(
            UUID drugId,
            String drugName,
            StockMovementType movementType,
            Integer quantity,
            UUID referenceId,
            UUID performedBy,
            String note
    ) {
        this.drugId = drugId;
        this.drugName = drugName;
        this.movementType = movementType;
        this.quantity = quantity;
        this.referenceId = referenceId;
        this.performedBy = performedBy;
        this.note = note;
        this.occurredAt = Instant.now();
    }
}