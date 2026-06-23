package com.custard.ehr.pharmacy;

import java.util.UUID;

public record DrugLookupView(
        UUID drugId,
        String name,
        String strength,
        String form
) {
}