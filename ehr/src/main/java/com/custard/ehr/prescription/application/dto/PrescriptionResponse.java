package com.custard.ehr.prescription.application.dto;

import com.custard.ehr.prescription.domain.Prescription;
import com.custard.ehr.prescription.domain.PrescriptionStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PrescriptionResponse(
        UUID id,
        UUID encounterId,
        UUID patientId,
        UUID prescribedBy,
        Instant prescribedAt,
        PrescriptionStatus status,
        String notes,
        List<PrescriptionItemResponse> items
) {
    public static PrescriptionResponse from(Prescription prescription) {
        return new PrescriptionResponse(
                prescription.getId(),
                prescription.getEncounterId(),
                prescription.getPatientId(),
                prescription.getPrescribedBy(),
                prescription.getPrescribedAt(),
                prescription.getStatus(),
                prescription.getNotes(),
                prescription.getItems()
                        .stream()
                        .map(PrescriptionItemResponse::from)
                        .toList()
        );
    }
}