package com.custard.ehr.prescription.application.dto;

import com.custard.ehr.prescription.domain.PrescriptionItem;

import java.util.UUID;

public record PrescriptionItemResponse(
        UUID id,
        UUID drugId,
        String drugName,
        String strength,
        String form,
        String dosage,
        String frequency,
        String duration,
        String route,
        Integer quantity,
        String instructions
) {
    public static PrescriptionItemResponse from(PrescriptionItem item) {
        return new PrescriptionItemResponse(
                item.getId(),
                item.getDrugId(),
                item.getDrugName(),
                item.getStrength(),
                item.getForm(),
                item.getDosage(),
                item.getFrequency(),
                item.getDuration(),
                item.getRoute(),
                item.getQuantity(),
                item.getInstructions()
        );
    }
}