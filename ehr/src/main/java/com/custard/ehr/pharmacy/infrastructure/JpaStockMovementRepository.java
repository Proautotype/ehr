package com.custard.ehr.pharmacy.infrastructure;

import com.custard.ehr.pharmacy.application.ports.StockMovementRepository;
import com.custard.ehr.pharmacy.domain.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaStockMovementRepository
        extends JpaRepository<StockMovement, UUID>, StockMovementRepository {
}