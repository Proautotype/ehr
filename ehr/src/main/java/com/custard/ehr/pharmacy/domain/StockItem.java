package com.custard.ehr.pharmacy.domain;

import com.custard.ehr.shared.domain.AuditableEntity;
import com.custard.ehr.shared.exception.BusinessException;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(
        name = "stock_items",
        indexes = {
                @Index(name = "idx_stock_drug", columnList = "product.id"),
                @Index(name = "idx_stock_drug_name", columnList = "product.name")
        }
)
public class StockItem extends AuditableEntity {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "batch_number")
    private String batchNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    private Drug product;

    // ✅ COST PRICE - lives on StockItem
    @Column(name = "unit_cost")
    private Double unitCost;

    // unitCost * quantity
    @Column(name = "total_cost")
    private Double totalCost;

    @Column(nullable = false)
    private Integer quantityAvailable;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    private Stock stock;

    protected StockItem() {
    }

    public StockItem(
            Integer quantityAvailable,
            String batchNumber,
            Drug product
    ) {
        if (quantityAvailable == null || quantityAvailable < 0) {
            throw new BusinessException("Stock quantity cannot be negative");
        }

        this.quantityAvailable = quantityAvailable;
        this.batchNumber = batchNumber;
    }

    public void deduct(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BusinessException("Quantity to deduct must be greater than zero");
        }

        if (quantityAvailable < quantity) {
            throw new BusinessException("Insufficient stock for " + product.getName());
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

    public Integer getQuantityAvailable() {
        return quantityAvailable;
    }

    public Drug getProduct() {
        return product;
    }

    @Override
    public String toString() {
        return "StockItem{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", batchNumber='" + batchNumber + '\'' +
                ", product=" + product +
                ", quantityAvailable=" + quantityAvailable +
                ", version=" + version +
                '}';
    }
}