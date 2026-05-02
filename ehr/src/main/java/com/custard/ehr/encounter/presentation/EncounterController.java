package com.custard.ehr.encounter.presentation;

import com.custard.ehr.encounter.application.dto.CreateEncounterRequest;
import com.custard.ehr.encounter.application.dto.EncounterResponse;
import com.custard.ehr.encounter.application.service.EncounterService;
import com.custard.ehr.shared.infrastruture.web.AppApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/encounters")
@Tag(name = "Encounters", description = "Patient encounter and workflow management APIs")
@SecurityRequirement(name = "bearerAuth")
public class EncounterController {

    private static final Logger log = LoggerFactory.getLogger(EncounterController.class);
    private final EncounterService encounterService;

    public EncounterController(EncounterService encounterService) {
        this.encounterService = encounterService;
    }

    @PostMapping
    @Operation(
            summary = "Create encounter",
            description = "Creates a new patient encounter"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Encounter created successfully",
                    content = @Content(schema = @Schema(implementation = EncounterResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<EncounterResponse> create(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Encounter creation payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateEncounterRequest.class))
            )
            @RequestBody CreateEncounterRequest request
    ) {
        log.info("REST request to create encounter for patient: {}", request.patientId());
        return AppApiResponse.success(
                "Encounter created successfully",
                encounterService.create(request)
        );
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get encounter by ID",
            description = "Fetch an encounter by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Encounter retrieved successfully",
                    content = @Content(schema = @Schema(implementation = EncounterResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Encounter not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<EncounterResponse> getById(@PathVariable UUID id) {
        log.debug("REST request to get encounter: {}", id);
        return AppApiResponse.success(encounterService.getById(id));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(
            summary = "Get encounters by patient",
            description = "Fetch all encounters associated with a patient"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Encounters retrieved successfully",
                    content = @Content(schema = @Schema(implementation = EncounterResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<List<EncounterResponse>> getByPatient(
            @PathVariable UUID patientId
    ) {
        log.debug("REST request to get encounters for patient: {}", patientId);
        return AppApiResponse.success(encounterService.getByPatient(patientId));
    }

    @GetMapping("/active")
    @Operation(
            summary = "Get active encounters",
            description = "Fetch all currently active encounters"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Active encounters retrieved successfully",
                    content = @Content(schema = @Schema(implementation = EncounterResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<List<EncounterResponse>> getActiveEncounters() {
        log.debug("REST request to get all active encounters");
        return AppApiResponse.success(encounterService.getActiveEncounters());
    }

    @PatchMapping("/{id}/complete")
    @Operation(
            summary = "Complete encounter",
            description = "Marks an encounter as completed"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Encounter completed successfully",
                    content = @Content(schema = @Schema(implementation = EncounterResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Encounter not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<EncounterResponse> complete(@PathVariable UUID id) {
        log.info("REST request to complete encounter: {}", id);
        return AppApiResponse.success(
                "Encounter completed successfully",
                encounterService.complete(id)
        );
    }

    @PatchMapping("/{id}/cancel")
    @Operation(
            summary = "Cancel encounter",
            description = "Cancels an encounter"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Encounter cancelled successfully",
                    content = @Content(schema = @Schema(implementation = EncounterResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Encounter not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<EncounterResponse> cancel(@PathVariable UUID id) {
        log.info("REST request to cancel encounter: {}", id);
        return AppApiResponse.success(
                "Encounter cancelled successfully",
                encounterService.cancel(id)
        );
    }

    @PatchMapping("/{id}/payment/paid")
    @Operation(
            summary = "Mark payment as paid",
            description = "Marks the encounter payment status as PAID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment marked as paid",
                    content = @Content(schema = @Schema(implementation = EncounterResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Encounter not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<EncounterResponse> markPaymentPaid(@PathVariable UUID id) {
        log.info("REST request to mark payment as PAID for encounter: {}", id);
        return AppApiResponse.success(encounterService.markPaymentPaid(id));
    }

    @PatchMapping("/{id}/payment/waive")
    @Operation(
            summary = "Waive payment",
            description = "Waives payment for an encounter"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment waived successfully",
                    content = @Content(schema = @Schema(implementation = EncounterResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Encounter not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<EncounterResponse> waivePayment(@PathVariable UUID id) {
        log.info("REST request to waive payment for encounter: {}", id);
        return AppApiResponse.success(encounterService.waivePayment(id));
    }

    @PatchMapping("/{id}/lab/pending")
    @Operation(
            summary = "Mark lab as pending",
            description = "Sets lab status to PENDING for an encounter"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lab marked as pending",
                    content = @Content(schema = @Schema(implementation = EncounterResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Encounter not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<EncounterResponse> markLabPending(@PathVariable UUID id) {
        log.debug("REST request to set lab status to PENDING for encounter: {}", id);
        return AppApiResponse.success(encounterService.markLabPending(id));
    }

    @PatchMapping("/{id}/lab/completed")
    @Operation(
            summary = "Mark lab as completed",
            description = "Sets lab status to COMPLETED for an encounter"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lab marked as completed",
                    content = @Content(schema = @Schema(implementation = EncounterResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Encounter not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<EncounterResponse> markLabCompleted(@PathVariable UUID id) {
        log.info("REST request to set lab status to COMPLETED for encounter: {}", id);
        return AppApiResponse.success(encounterService.markLabCompleted(id));
    }

    @PatchMapping("/{id}/pharmacy/pending")
    @Operation(
            summary = "Mark pharmacy as pending",
            description = "Sets pharmacy status to PENDING for an encounter"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pharmacy marked as pending",
                    content = @Content(schema = @Schema(implementation = EncounterResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Encounter not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<EncounterResponse> markPharmacyPending(@PathVariable UUID id) {
        log.debug("REST request to set pharmacy status to PENDING for encounter: {}", id);
        return AppApiResponse.success(encounterService.markPharmacyPending(id));
    }

    @PatchMapping("/{id}/pharmacy/dispensed")
    @Operation(
            summary = "Mark pharmacy as dispensed",
            description = "Sets pharmacy status to DISPENSED for an encounter"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pharmacy marked as dispensed",
                    content = @Content(schema = @Schema(implementation = EncounterResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Encounter not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<EncounterResponse> markPharmacyDispensed(@PathVariable UUID id) {
        log.info("REST request to set pharmacy status to DISPENSED for encounter: {}", id);
        return AppApiResponse.success(encounterService.markPharmacyDispensed(id));
    }
}