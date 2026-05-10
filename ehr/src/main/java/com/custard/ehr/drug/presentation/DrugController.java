package com.custard.ehr.drug.presentation;

import com.custard.ehr.drug.application.dto.DrugResponse;
import com.custard.ehr.drug.application.service.DrugService;
import com.custard.ehr.shared.domain.PageResultDto;
import com.custard.ehr.shared.infrastruture.web.AppApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/drugs")
@Slf4j
@Tag(name = "Drugs", description = "Drug catalog and search APIs")
@SecurityRequirement(name = "bearerAuth")
public class DrugController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final DrugService drugService;

    public DrugController(DrugService drugService) {
        this.drugService = drugService;
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search drugs",
            description = "Search drug catalog by name, code, or keyword"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Search results retrieved successfully",
                    content = @Content(schema = @Schema(implementation = DrugResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid search query"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<PageResultDto<DrugResponse>> search(
            @RequestParam(value = "query") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy
    ) {
        log.info("Drug search parameters q {}, p {}, s {}", query,  page, size);
        return AppApiResponse.success(drugService.search(query, size, page, sortBy));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get drug by ID",
            description = "Fetch a drug by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Drug retrieved successfully",
                    content = @Content(schema = @Schema(implementation = DrugResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Drug not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<DrugResponse> get(@PathVariable UUID id) {
        log.debug("Fetching drug ID: {}", id);
        return AppApiResponse.success(drugService.get(id));
    }
}