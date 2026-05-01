package com.custard.ehr.shared.domain;

import java.util.UUID;

public record EncounterLookupView(
        UUID encounterId,
        UUID patientId
) {
}