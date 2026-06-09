package com.custard.ehr.payment.application.dto;

import com.custard.ehr.payment.domain.InvoiceItem;

import java.math.BigDecimal;
import java.util.UUID;

public record InvoiceItemResponse(
        UUID id,
        InvoiceItemType invoiceItemType,
        BigDecimal amount,
        int quantity,
        BigDecimal unitPrice,
        String itemReferenceId
) {
    public static InvoiceItemResponse from(InvoiceItem item) {
        return new InvoiceItemResponse(
                item.getId(),
                item.getInvoiceItemType(),
                item.getAmount(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getItemReferenceId()
        );
    }
}