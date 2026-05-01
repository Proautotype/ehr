package com.custard.ehr.pharmacy.infrastructure;

import com.custard.ehr.pharmacy.application.ports.StockRepository;
import com.custard.ehr.pharmacy.domain.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaStockRepository
        extends JpaRepository<StockItem, UUID>, StockRepository {

    Optional<StockItem> findByDrugId(UUID drugId);

    List<StockItem> findTop20ByDrugNameContainingIgnoreCase(String drugName);
}