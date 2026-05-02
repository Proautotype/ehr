package com.custard.ehr.prescription;

import java.util.List;
import java.util.UUID;

public record PrescriptionLookupView(
        UUID prescriptionId,
        UUID encounterId,
        UUID patientId,
        List<PrescriptionItemLookupView> items
) {
}