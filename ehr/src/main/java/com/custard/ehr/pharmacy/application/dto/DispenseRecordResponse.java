package com.custard.ehr.pharmacy.application.dto;

import com.custard.ehr.pharmacy.domain.DispenseRecord;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record DispenseRecordResponse(
        UUID id,
        UUID prescriptionId,
        UUID encounterId,
        UUID patientId,
        UUID dispensedBy,
        Instant dispensedAt,
        boolean partial,
        List<DispenseItemResponse> items
) {
    public static DispenseRecordResponse from(DispenseRecord record) {
        return new DispenseRecordResponse(
                record.getId(),
                record.getPrescriptionId(),
                record.getEncounterId(),
                record.getPatientId(),
                record.getDispensedBy(),
                record.getDispensedAt(),
                record.isPartial(),
                record.getItems()
                        .stream()
                        .map(DispenseItemResponse::from)
                        .toList()
        );
    }
}