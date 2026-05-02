package com.custard.ehr.laboratory.application.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateLabOrderRequest(
        @NotNull UUID encounterId,
        String clinicalNote,
        @NotEmpty List<UUID> labTestIds
) {
}