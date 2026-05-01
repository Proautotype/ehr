package com.custard.ehr.encounter;

import java.util.UUID;

public record EncounterLookupView(
        UUID encounterId,
        UUID patientId
) {
}