package com.custard.ehr.shared.events;

import java.time.Instant;
import java.util.UUID;

public record VitalsRecordedEvent(
        UUID vitalsId,
        UUID encounterId,
        UUID patientId,
        UUID recordedBy,
        Instant recordedAt
) {
}