package com.custard.ehr.pharmacy.application.ports;

import com.custard.ehr.pharmacy.domain.StockMovement;

public interface StockMovementRepository {

    StockMovement save(StockMovement movement);
}