package com.custard.ehr.laboratory.domain;

import com.custard.ehr.shared.domain.AuditableEntity;
import com.custard.ehr.shared.exception.BusinessException;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
        name = "lab_tests",
        indexes = {
                @Index(name = "idx_lab_test_name", columnList = "name")
        }
)
public class LabTest extends AuditableEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @Column(nullable = false, unique = true)
    private String name;

    private String code;

    @Column(length = 1000)
    private String description;

    private BigDecimal price;

    @Column(nullable = false)
    private boolean active = true;

    protected LabTest() {
    }

    public LabTest(String name, String code, String description, BigDecimal price) {
        if (name == null || name.isBlank()) {
            throw new BusinessException("Lab test name is required");
        }

        this.name = name;
        this.code = code;
        this.description = description;
        this.price = price == null ? BigDecimal.ZERO : price;
    }

    public void deactivate() {
        this.active = false;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean isActive() {
        return active;
    }
}