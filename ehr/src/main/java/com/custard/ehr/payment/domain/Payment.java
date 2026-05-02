package com.custard.ehr.payment.domain;

import com.custard.ehr.shared.domain.AuditableEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "payments",
        indexes = {
                @Index(name = "idx_payment_invoice", columnList = "invoiceId"),
                @Index(name = "idx_payment_patient", columnList = "patientId")
        }
)
public class Payment extends AuditableEntity {

    @Id
    private UUID id = UUID.randomUUID();

    @Column(nullable = false)
    private UUID invoiceId;

    @Column(nullable = false)
    private UUID encounterId;

    @Column(nullable = false)
    private UUID patientId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    private String reference;

    @Column(nullable = false)
    private UUID receivedBy;

    @Column(nullable = false)
    private Instant paidAt;

    protected Payment() {
    }

    public Payment(
            UUID invoiceId,
            UUID encounterId,
            UUID patientId,
            BigDecimal amount,
            PaymentMethod method,
            String reference,
            UUID receivedBy
    ) {
        this.invoiceId = invoiceId;
        this.encounterId = encounterId;
        this.patientId = patientId;
        this.amount = amount;
        this.method = method;
        this.reference = reference;
        this.receivedBy = receivedBy;
        this.paidAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getInvoiceId() { return invoiceId; }
    public UUID getEncounterId() { return encounterId; }
    public UUID getPatientId() { return patientId; }
    public BigDecimal getAmount() { return amount; }
    public PaymentMethod getMethod() { return method; }
    public String getReference() { return reference; }
    public UUID getReceivedBy() { return receivedBy; }
    public Instant getPaidAt() { return paidAt; }
}