package com.custard.ehr.payment.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateInvoiceRequest(
        @NotNull UUID encounterId,
        @NotEmpty List<@Valid InvoiceItemRequest> items
) {
}