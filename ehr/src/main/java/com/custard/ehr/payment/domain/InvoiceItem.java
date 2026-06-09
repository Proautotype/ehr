package com.custard.ehr.payment.domain;

import com.custard.ehr.payment.application.dto.InvoiceItemType;
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
    private String patientId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InvoiceItemType invoiceItemType;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false, name = "item_reference_id")
    private String itemReferenceId;

    @Column(nullable = false, name = "unit_price")
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column(nullable = false)
    private int quantity = 1;

    protected InvoiceItem() {
    }

    public InvoiceItem(Invoice invoice, InvoiceItemType invoiceItemType, BigDecimal unitPrice) {
        if (invoiceItemType == null) {
            throw new BusinessException("Invoice item description is required");
        }

        if (unitPrice == null || unitPrice.signum() <= 0) {
            throw new BusinessException("Invoice item amount must be greater than zero");
        }

        this.invoice = invoice;
        this.invoiceItemType = invoiceItemType;
        this.unitPrice = amount;
    }

    public UUID getId() { return id; }
    public InvoiceItemType getInvoiceItemType() { return invoiceItemType; }
    public BigDecimal getAmount() { return amount; }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public void setInvoiceItemType(InvoiceItemType invoiceItemType) {
        this.invoiceItemType = invoiceItemType;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getItemReferenceId() {
        return itemReferenceId;
    }

    public void setItemReferenceId(String itemReferenceId) {
        this.itemReferenceId = itemReferenceId;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}