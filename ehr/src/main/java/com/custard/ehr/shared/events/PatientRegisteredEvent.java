package com.custard.ehr.shared.events;

import java.time.Instant;
import java.util.UUID;
public record PatientRegisteredEvent(
        UUID patientId,
        String patientNumber,
        UUID registeredBy,
        Instant registeredAt
) {
}