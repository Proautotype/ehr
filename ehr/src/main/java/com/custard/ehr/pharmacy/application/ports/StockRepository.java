package com.custard.ehr.pharmacy.application.ports;

import com.custard.ehr.pharmacy.domain.StockItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockRepository {

    StockItem save(StockItem stockItem);

    Optional<StockItem> findByDrugId(UUID drugId);

    List<StockItem> findTop20ByDrugNameContainingIgnoreCase(String drugName);
}