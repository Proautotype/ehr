package com.custard.ehr.pharmacy.application.ports;

import com.custard.ehr.pharmacy.application.dto.DrugStockItemDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductStockQuery {
    Page<List<DrugStockItemDto>> getProducts();
    Page<List<DrugStockItemDto>> searchProducts(String searchTerm);
}
