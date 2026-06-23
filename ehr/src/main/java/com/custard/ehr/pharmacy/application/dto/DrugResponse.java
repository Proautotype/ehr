package com.custard.ehr.pharmacy.application.dto;

import com.custard.ehr.pharmacy.domain.Drug;

import java.util.UUID;

public record DrugResponse(
        UUID id,
        String name,
        String strength,
        String form
) {
    public static DrugResponse from(Drug drug) {
        return new DrugResponse(
                drug.getId(),
                drug.getName(),
                drug.getStrength(),
                drug.getForm()
        );
    }
}