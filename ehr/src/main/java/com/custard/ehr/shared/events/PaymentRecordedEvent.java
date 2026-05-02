package com.custard.ehr.shared.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentRecordedEvent(
        UUID paymentId,
        UUID invoiceId,
        UUID encounterId,
        UUID patientId,
        BigDecimal amount,
        String method,
        UUID receivedBy,
        Instant paidAt
) {
}