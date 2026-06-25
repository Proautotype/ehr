package com.custard.ehr.drug.application.dto;

import com.custard.ehr.shared.domain.DrugStockItemStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
public class DrugStockItemDto {
    private UUID id;
    private String name;
    private Long quantity;
    private DrugStockItemStatus status;

    public DrugStockItemDto(UUID id, String name, Long quantity, DrugStockItemStatus status) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.status = status;
    }

    // Optional: Helper getter for int
    public int getQuantityAsInt() {
        return quantity != null ? quantity.intValue() : 0;
    }
}
