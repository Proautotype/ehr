package com.custard.ehr.pharmacy.presentation;

import com.custard.ehr.shared.infrastruture.web.AppApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stocks")
@Tag(name = "Stocks", description = "Stock management")
public class StocksController {

    private final Logger logger = LoggerFactory.getLogger(StocksController.class);

    @PostMapping("/create")
    @Operation(
            summary = "Create a new stock",
            description = "Create a new stock with stock items"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Stock and stock items created"
            )
    })
    public AppApiResponse<String> createStock() {
        return AppApiResponse.success("Success");
    }

}
