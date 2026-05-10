package com.custard.ehr.prescription;

import java.util.UUID;

public record PrescriptionItemLookupView(
        UUID prescriptionItemId,
        UUID drugId,
        String drugName,
        String strength,
        String form,
        Integer prescribedQuantity
) {
}