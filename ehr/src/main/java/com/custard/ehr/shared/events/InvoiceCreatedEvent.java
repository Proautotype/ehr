package com.custard.ehr.shared.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record InvoiceCreatedEvent(
        UUID invoiceId,
        UUID encounterId,
        UUID patientId,
        BigDecimal totalAmount,
        UUID createdBy,
        Instant createdAt
) {
}