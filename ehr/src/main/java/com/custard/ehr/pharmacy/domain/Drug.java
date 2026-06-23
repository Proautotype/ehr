package com.custard.ehr.pharmacy.domain;

import com.custard.ehr.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "drugs",
        indexes = {
                @Index(name = "idx_drug_name", columnList = "name")
        }
)
@Getter
public class Drug extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    private String strength;

    private String form; // tablet, syrup, injection

    private boolean active = true;

    private String description;
    private String code;
    private String unit;
    private BigDecimal currentSellingPrice;

    @OneToMany
    private List<Supplier> suppliers;

    @OneToMany
    private List<StockItem> stockItems;

    protected Drug() {}

    public Drug(String name, String strength, String form) {
        this.name = name;
        this.strength = strength;
        this.form = form;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStrength() {
        return strength;
    }

    public String getForm() {
        return form;
    }

    public boolean isActive() {
        return active;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getCurrentSellingPrice() {
        return currentSellingPrice;
    }

    public void setCurrentSellingPrice(BigDecimal currentSellingPrice) {
        this.currentSellingPrice = currentSellingPrice;
    }

    public void setUnit(String unit) {
        this.unit = unit;


    }
}