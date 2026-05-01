package com.custard.ehr.shared.events;

import java.time.Instant;
import java.util.UUID;

public record PrescriptionDispensedEvent(
        UUID dispenseRecordId,
        UUID prescriptionId,
        UUID encounterId,
        UUID patientId,
        UUID dispensedBy,
        boolean partial,
        Instant dispensedAt
) {
}