package com.custard.ehr.shared.events;

import java.time.Instant;
import java.util.UUID;

public record ConsultationRecordedEvent(
        UUID consultationId,
        UUID encounterId,
        UUID patientId,
        UUID doctorId,
        Instant recordedAt
) {
}