package com.custard.ehr.pharmacy.application.dto;

import com.custard.ehr.pharmacy.domain.DispenseItem;
import com.custard.ehr.pharmacy.domain.DispenseItemStatus;
import com.custard.ehr.pharmacy.domain.NonDispenseReason;

import java.util.UUID;

public record DispenseItemResponse(
        UUID id,
        UUID prescriptionItemId,
        UUID drugId,
        String drugName,
        Integer prescribedQuantity,
        Integer dispensedQuantity,
        DispenseItemStatus status,
        NonDispenseReason reason,
        String note
) {
    public static DispenseItemResponse from(DispenseItem item) {
        return new DispenseItemResponse(
                item.getId(),
                item.getPrescriptionItemId(),
                item.getDrugId(),
                item.getDrugName(),
                item.getPrescribedQuantity(),
                item.getDispensedQuantity(),
                item.getStatus(),
                item.getReason(),
                item.getNote()
        );
    }
}