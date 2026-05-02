package com.custard.ehr.prescription.presentation;

import com.custard.ehr.prescription.application.dto.CreatePrescriptionRequest;
import com.custard.ehr.prescription.application.dto.PrescriptionResponse;
import com.custard.ehr.prescription.application.service.PrescriptionService;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/prescriptions")
@Slf4j
@Tag(name = "Prescriptions", description = "Prescription creation, tracking, and pharmacy queue APIs")
@SecurityRequirement(name = "bearerAuth")
public class PrescriptionController {

    private final Logger log = LoggerFactory.getLogger(PrescriptionController.class);

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping
    @Operation(
            summary = "Create prescription",
            description = "Creates a prescription for an encounter and sends it to the pharmacy queue"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Prescription created successfully",
                    content = @Content(schema = @Schema(implementation = PrescriptionResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Encounter not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<PrescriptionResponse> create(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Prescription creation payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreatePrescriptionRequest.class))
            )
            @RequestBody CreatePrescriptionRequest request
    ) {
        log.info("Received request to create prescription for encounter {}", request.encounterId());

        return AppApiResponse.success(
                "Prescription created and sent to pharmacy",
                prescriptionService.create(request)
        );
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get prescription by ID",
            description = "Fetch a prescription by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Prescription retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PrescriptionResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Prescription not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<PrescriptionResponse> getById(@PathVariable UUID id) {
        log.debug("Received request to fetch prescription ID: {}", id);
        return AppApiResponse.success(prescriptionService.getById(id));
    }

    @GetMapping("/encounter/{encounterId}")
    @Operation(
            summary = "Get prescriptions by encounter",
            description = "Fetch all prescriptions associated with a specific encounter"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Prescriptions retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PrescriptionResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Encounter not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<List<PrescriptionResponse>> getByEncounter(
            @PathVariable UUID encounterId
    ) {
        log.debug("Received request to fetch prescriptions for encounter ID: {}", encounterId);
        return AppApiResponse.success(prescriptionService.getByEncounter(encounterId));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(
            summary = "Get prescriptions by patient",
            description = "Fetch all prescriptions for a specific patient"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Prescriptions retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PrescriptionResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<List<PrescriptionResponse>> getByPatient(
            @PathVariable UUID patientId
    ) {
        log.debug("Received request to fetch prescriptions for patient ID: {}", patientId);
        return AppApiResponse.success(prescriptionService.getByPatient(patientId));
    }

    @GetMapping("/pharmacy-queue")
    @Operation(
            summary = "Get pharmacy queue",
            description = "Fetch prescriptions currently waiting for pharmacy processing"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pharmacy queue retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PrescriptionResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<List<PrescriptionResponse>> getPharmacyQueue() {
        log.debug("Received request to fetch pharmacy prescription queue");
        return AppApiResponse.success(prescriptionService.getPharmacyQueue());
    }

    @PatchMapping("/{id}/cancel")
    @Operation(
            summary = "Cancel prescription",
            description = "Cancels a prescription by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Prescription cancelled successfully",
                    content = @Content(schema = @Schema(implementation = PrescriptionResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Prescription not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<PrescriptionResponse> cancel(@PathVariable UUID id) {
        log.info("Received request to cancel prescription ID: {}", id);

        return AppApiResponse.success(
                "Prescription cancelled successfully",
                prescriptionService.cancel(id)
        );
    }
}