package com.custard.ehr.shared.events;

import java.time.Instant;
import java.util.UUID;

public record PrescriptionCreatedEvent(
        UUID prescriptionId,
        UUID encounterId,
        UUID patientId,
        UUID prescribedBy,
        Instant prescribedAt
) {
}