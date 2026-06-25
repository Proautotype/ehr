package com.custard.ehr.drug;

import java.util.UUID;

public record DrugLookupView(
        UUID drugId,
        String name,
        String strength,
        String form
) {
}