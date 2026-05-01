package com.custard.ehr.encounter.application.dto;

import com.custard.ehr.encounter.domain.Encounter;
import com.custard.ehr.encounter.domain.EncounterStatus;
import com.custard.ehr.encounter.domain.LabStatus;
import com.custard.ehr.encounter.domain.PaymentStatus;
import com.custard.ehr.encounter.domain.PharmacyStatus;

import java.time.Instant;
import java.util.UUID;

public record EncounterResponse(
        UUID id,
        UUID patientId,
        UUID openedBy,
        UUID completedBy,
        Instant openedAt,
        Instant completedAt,
        EncounterStatus status,
        PaymentStatus paymentStatus,
        LabStatus labStatus,
        PharmacyStatus pharmacyStatus,
        String reasonForVisit
) {
    public static EncounterResponse from(Encounter encounter) {
        return new EncounterResponse(
                encounter.getId(),
                encounter.getPatientId(),
                encounter.getOpenedBy(),
                encounter.getCompletedBy(),
                encounter.getOpenedAt(),
                encounter.getCompletedAt(),
                encounter.getStatus(),
                encounter.getPaymentStatus(),
                encounter.getLabStatus(),
                encounter.getPharmacyStatus(),
                encounter.getReasonForVisit()
        );
    }
}