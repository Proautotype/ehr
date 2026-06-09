package com.custard.ehr.payment.domain;

import com.custard.ehr.payment.application.dto.InvoiceItemType;
import com.custard.ehr.shared.domain.AuditableEntity;
import com.custard.ehr.shared.exception.BusinessException;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Entity
@Table(
        name = "invoices",
        indexes = {
                @Index(name = "idx_invoice_encounter", columnList = "encounterId"),
                @Index(name = "idx_invoice_patient", columnList = "patientId"),
                @Index(name = "idx_invoice_status", columnList = "status")
        }
)
public class Invoice extends AuditableEntity {

    @Id
    private UUID id = UUID.randomUUID();

    private UUID encounterId;

    @Column(nullable = false)
    private UUID patientId;

    @Column(nullable = false)
    private String patientNumber;

    @Column(nullable = false)
    private UUID createdBy;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status = InvoiceStatus.UNPAID;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> items = new ArrayList<>();

    @Version
    private Long version;

    protected Invoice() {
    }

    public Invoice(UUID encounterId, UUID patientId, UUID createdBy) {
        this.encounterId = encounterId;
        this.patientId = patientId;
        this.createdBy = createdBy;
        this.createdAt = Instant.now();
    }

    public void addItem(InvoiceItemType description, BigDecimal amount) {
        if (status != InvoiceStatus.UNPAID) {
            throw new BusinessException("Cannot add item to invoice after payment has started");
        }

        InvoiceItem item = new InvoiceItem(this, description, amount);
        items.add(item);
        recalculateTotal();
    }

    public void addItem(InvoiceItem invoiceItem) {
        if (status != InvoiceStatus.UNPAID) {
            throw new BusinessException("Cannot add item to invoice after payment has started");
        }

        invoiceItem.setInvoice(this);

        items.add(invoiceItem);
        recalculateTotal();
    }

    public void applyPayment(BigDecimal amount) {
        if (status == InvoiceStatus.PAID || status == InvoiceStatus.WAIVED) {
            throw new BusinessException("Invoice is already closed");
        }

        if (amount == null || amount.signum() <= 0) {
            throw new BusinessException("Payment amount must be greater than zero");
        }

        BigDecimal remaining = totalAmount.subtract(amountPaid);

        if (amount.compareTo(remaining) > 0) {
            throw new BusinessException("Payment amount exceeds outstanding balance");
        }

        this.amountPaid = this.amountPaid.add(amount);

        if (amountPaid.compareTo(totalAmount) == 0) {
            this.status = InvoiceStatus.PAID;
        } else {
            this.status = InvoiceStatus.PARTIALLY_PAID;
        }
    }

    public void waive() {
        if (status == InvoiceStatus.PAID) {
            throw new BusinessException("Paid invoice cannot be waived");
        }

        this.status = InvoiceStatus.WAIVED;
    }

    private void recalculateTotal() {
        this.totalAmount = items.stream()
                .map(InvoiceItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public UUID getId() { return id; }
    public UUID getEncounterId() { return encounterId; }
    public UUID getPatientId() { return patientId; }
    public UUID getCreatedBy() { return createdBy; }
    public Instant getCreatedAt() { return createdAt; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public BigDecimal getAmountPaid() { return amountPaid; }
    public InvoiceStatus getStatus() { return status; }
    public List<InvoiceItem> getItems() { return Collections.unmodifiableList(items); }

    public void setEncounterId(UUID encounterId) {
        this.encounterId = encounterId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public String getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public void setItems(List<InvoiceItem> items) {
        this.items = items;
    }
}