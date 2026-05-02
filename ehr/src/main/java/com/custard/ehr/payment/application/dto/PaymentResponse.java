package com.custard.ehr.payment.application.dto;

import com.custard.ehr.payment.domain.Payment;
import com.custard.ehr.payment.domain.PaymentMethod;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID invoiceId,
        UUID encounterId,
        UUID patientId,
        BigDecimal amount,
        PaymentMethod method,
        String reference,
        UUID receivedBy,
        Instant paidAt
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getInvoiceId(),
                payment.getEncounterId(),
                payment.getPatientId(),
                payment.getAmount(),
                payment.getMethod(),
                payment.getReference(),
                payment.getReceivedBy(),
                payment.getPaidAt()
        );
    }
}