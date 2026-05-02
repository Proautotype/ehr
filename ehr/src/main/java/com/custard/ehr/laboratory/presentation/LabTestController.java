package com.custard.ehr.laboratory.presentation;

import com.custard.ehr.laboratory.application.dto.CreateLabTestRequest;
import com.custard.ehr.laboratory.application.dto.LabTestResponse;
import com.custard.ehr.laboratory.application.service.LabTestService;
import com.custard.ehr.shared.infrastruture.web.AppApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lab-tests")
@Slf4j
@Tag(name = "Lab Tests", description = "Laboratory test catalog management APIs")
@SecurityRequirement(name = "bearerAuth")
public class LabTestController {

    private final LabTestService labTestService;

    public LabTestController(LabTestService labTestService) {
        this.labTestService = labTestService;
    }

    @PostMapping
    @Operation(
            summary = "Create lab test",
            description = "Creates a new laboratory test definition"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lab test created successfully",
                    content = @Content(schema = @Schema(implementation = LabTestResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<LabTestResponse> create(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Lab test creation payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateLabTestRequest.class))
            )
            @RequestBody CreateLabTestRequest request
    ) {
        return AppApiResponse.success(
                "Lab test created successfully",
                labTestService.create(request)
        );
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get lab test by ID",
            description = "Fetch a laboratory test by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lab test retrieved successfully",
                    content = @Content(schema = @Schema(implementation = LabTestResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Lab test not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<LabTestResponse> getById(@PathVariable UUID id) {
        return AppApiResponse.success(labTestService.getById(id));
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search lab tests",
            description = "Search laboratory test catalog by name or keyword"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Search results retrieved successfully",
                    content = @Content(schema = @Schema(implementation = LabTestResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid search query"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<List<LabTestResponse>> search(@RequestParam String query) {
        return AppApiResponse.success(labTestService.search(query));
    }
}