package com.custard.ehr.payment.application.dto;

import com.custard.ehr.payment.domain.InvoiceItem;

import java.math.BigDecimal;
import java.util.UUID;

public record InvoiceItemResponse(
        UUID id,
        String description,
        BigDecimal amount
) {
    public static InvoiceItemResponse from(InvoiceItem item) {
        return new InvoiceItemResponse(
                item.getId(),
                item.getDescription(),
                item.getAmount()
        );
    }
}