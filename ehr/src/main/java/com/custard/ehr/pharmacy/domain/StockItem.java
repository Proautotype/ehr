package com.custard.ehr.pharmacy.domain;

import com.custard.ehr.shared.domain.AuditableEntity;
import com.custard.ehr.shared.exception.BusinessException;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(
        name = "stock_items",
        indexes = {
                @Index(name = "idx_stock_drug", columnList = "drugId"),
                @Index(name = "idx_stock_drug_name", columnList = "drugName")
        }
)
public class StockItem extends AuditableEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @Column(nullable = false, unique = true)
    private UUID drugId;

    @Column(nullable = false)
    private String drugName;

    private String strength;

    private String form;

    @Column(nullable = false)
    private Integer quantityAvailable;

    @Version
    private Long version;

    protected StockItem() {
    }

    public StockItem(
            UUID drugId,
            String drugName,
            String strength,
            String form,
            Integer quantityAvailable
    ) {
        if (quantityAvailable == null || quantityAvailable < 0) {
            throw new BusinessException("Stock quantity cannot be negative");
        }

        this.drugId = drugId;
        this.drugName = drugName;
        this.strength = strength;
        this.form = form;
        this.quantityAvailable = quantityAvailable;
    }

    public void deduct(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BusinessException("Quantity to deduct must be greater than zero");
        }

        if (quantityAvailable < quantity) {
            throw new BusinessException("Insufficient stock for " + drugName);
        }

        this.quantityAvailable -= quantity;
    }

    public void add(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BusinessException("Quantity to add must be greater than zero");
        }

        this.quantityAvailable += quantity;
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

    public Integer getQuantityAvailable() {
        return quantityAvailable;
    }
}