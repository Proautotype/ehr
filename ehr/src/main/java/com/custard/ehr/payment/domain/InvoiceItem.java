package com.custard.ehr.payment.domain;

import com.custard.ehr.shared.domain.AuditableEntity;
import com.custard.ehr.shared.exception.BusinessException;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "invoice_items")
public class InvoiceItem extends AuditableEntity {

    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal amount;

    protected InvoiceItem() {
    }

    InvoiceItem(Invoice invoice, String description, BigDecimal amount) {
        if (description == null || description.isBlank()) {
            throw new BusinessException("Invoice item description is required");
        }

        if (amount == null || amount.signum() <= 0) {
            throw new BusinessException("Invoice item amount must be greater than zero");
        }

        this.invoice = invoice;
        this.description = description;
        this.amount = amount;
    }

    public UUID getId() { return id; }
    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
}