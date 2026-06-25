package com.custard.ehr.pharmacy.presentation;

import com.custard.ehr.pharmacy.application.dto.AddSupplierDto;
import com.custard.ehr.shared.infrastruture.web.AppApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/supplier")
@Tag(name = "Supplier Controller", description = "Supplier controller responsible maintaining pharmacy supplier details (create, update, delete and view)")
public class SupplierController {

    public AppApiResponse<String> createSupplier(
             @Valid @RequestBody AddSupplierDto body
            ) {

    }

}
