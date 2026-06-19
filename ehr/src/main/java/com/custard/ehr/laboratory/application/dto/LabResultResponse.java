package com.custard.ehr.laboratory.application.dto;

import com.custard.ehr.laboratory.domain.LabResult;

import java.time.Instant;
import java.util.UUID;

public record LabResultResponse(
        UUID id,
        String resultValue,
        String referenceRange,
        String interpretation,
        UUID recordedBy,
        Instant recordedAt
) {
    public static LabResultResponse from(LabResult result) {
        if (result == null) {
            return null;
        }

        return new LabResultResponse(
                result.getId(),
                result.getResultValue(),
                result.getReferenceRange(),
                result.getInterpretation(),
                result.getRecordedBy(),
                result.getRecordedAt()
        );
    }
}