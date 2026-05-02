package com.custard.ehr.shared.events;

import java.time.Instant;
import java.util.UUID;

public record InvoiceWaivedEvent(
        UUID invoiceId,
        UUID encounterId,
        UUID patientId,
        UUID waivedBy,
        Instant waivedAt
) {
}