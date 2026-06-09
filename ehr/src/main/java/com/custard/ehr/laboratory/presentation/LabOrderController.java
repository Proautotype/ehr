package com.custard.ehr.laboratory.presentation;

import com.custard.ehr.laboratory.application.dto.CreateLabOrderRequest;
import com.custard.ehr.laboratory.application.dto.LabOrderResponse;
import com.custard.ehr.laboratory.application.dto.RecordLabResultRequest;
import com.custard.ehr.laboratory.application.dto.external.LabOrderPatientProjectionResponse;
import com.custard.ehr.laboratory.application.service.LabOrderService;
import com.custard.ehr.laboratory.application.service.LabResultService;
import com.custard.ehr.shared.infrastruture.web.AppApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lab-orders")
@Slf4j
@Tag(name = "Laboratory", description = "Laboratory orders and results management APIs")
@SecurityRequirement(name = "bearerAuth")
public class LabOrderController {

    private final LabOrderService labOrderService;
    private final LabResultService labResultService;

    public LabOrderController(
            LabOrderService labOrderService,
            LabResultService labResultService
    ) {
        this.labOrderService = labOrderService;
        this.labResultService = labResultService;
    }

    @PostMapping
    @Operation(
            summary = "Create lab order",
            description = "Creates a new laboratory order for an encounter"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lab order created successfully",
                    content = @Content(schema = @Schema(implementation = LabOrderResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Encounter not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<LabOrderResponse> create(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Lab order creation payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateLabOrderRequest.class))
            )
            @RequestBody CreateLabOrderRequest request
    ) {
        return AppApiResponse.success(
                "Lab order created successfully",
                labOrderService.create(request)
        );
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get lab order by ID",
            description = "Fetch a laboratory order by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lab order retrieved successfully",
                    content = @Content(schema = @Schema(implementation = LabOrderResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Lab order not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<LabOrderResponse> getById(@PathVariable UUID id) {
        return AppApiResponse.success(labOrderService.getById(id));
    }

    @GetMapping("/encounter/{encounterId}")
    @Operation(
            summary = "Get lab orders by encounter",
            description = "Fetch all lab orders associated with a specific encounter"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lab orders retrieved successfully",
                    content = @Content(schema = @Schema(implementation = LabOrderResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Encounter not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<List<LabOrderResponse>> getByEncounter(
            @PathVariable UUID encounterId
    ) {
        return AppApiResponse.success(labOrderService.getByEncounter(encounterId));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(
            summary = "Get lab orders by patient",
            description = "Fetch all lab orders for a specific patient"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lab orders retrieved successfully",
                    content = @Content(schema = @Schema(implementation = LabOrderResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<List<LabOrderResponse>> getByPatient(
            @PathVariable UUID patientId
    ) {
        return AppApiResponse.success(labOrderService.getByPatient(patientId));
    }

    @PostMapping("/{labOrderId}/results")
    @Operation(
            summary = "Record lab result",
            description = "Records results for a given lab order"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Lab result recorded successfully",
                    content = @Content(schema = @Schema(implementation = LabOrderResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Lab order not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<LabOrderResponse> recordResult(
            @PathVariable UUID labOrderId,
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Lab result payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RecordLabResultRequest.class))
            )
            @RequestBody RecordLabResultRequest request
    ) {
        return AppApiResponse.success(
                "Lab result recorded successfully",
                labResultService.recordResult(labOrderId, request)
        );
    }

    @GetMapping("/status/{status}")
    @Operation(
            summary = "Get Laboratory orders by status",
            description = "Use this handler to get all lab orders with requested status"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Return a list of Lab Orders"

            ),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<List<LabOrderPatientProjectionResponse>> getByLabStatus(@PathVariable("status") String status) {
        return AppApiResponse.success(labOrderService.findLabOrdersByStatus(status.toUpperCase()));
    }

}