package com.custard.ehr.payment.application.dto;

import com.custard.ehr.payment.domain.Invoice;
import com.custard.ehr.payment.domain.InvoiceStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record InvoiceResponse(
        UUID id,
        UUID encounterId,
        UUID patientId,
        UUID createdBy,
        Instant createdAt,
        BigDecimal totalAmount,
        BigDecimal amountPaid,
        InvoiceStatus status,
        List<InvoiceItemResponse> items
) {
    public static InvoiceResponse from(Invoice invoice) {
        return new InvoiceResponse(
                invoice.getId(),
                invoice.getEncounterId(),
                invoice.getPatientId(),
                invoice.getCreatedBy(),
                invoice.getCreatedAt(),
                invoice.getTotalAmount(),
                invoice.getAmountPaid(),
                invoice.getStatus(),
                invoice.getItems()
                        .stream()
                        .map(InvoiceItemResponse::from)
                        .toList()
        );
    }
}