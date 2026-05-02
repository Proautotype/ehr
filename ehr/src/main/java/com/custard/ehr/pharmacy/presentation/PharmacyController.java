package com.custard.ehr.pharmacy.presentation;

import com.custard.ehr.pharmacy.application.dto.DispensePrescriptionRequest;
import com.custard.ehr.pharmacy.application.dto.DispenseRecordResponse;
import com.custard.ehr.pharmacy.application.services.DispensingService;
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
@RequestMapping("/api/v1/pharmacy")
@Slf4j
@Tag(name = "Pharmacy", description = "Prescription dispensing and pharmacy operations APIs")
@SecurityRequirement(name = "bearerAuth")
public class PharmacyController {

    private final Logger log = LoggerFactory.getLogger(PharmacyController.class);

    private final DispensingService dispensingService;

    public PharmacyController(DispensingService dispensingService) {
        this.dispensingService = dispensingService;
    }

    @PostMapping("/dispense")
    @Operation(
            summary = "Dispense prescription",
            description = "Processes dispensing of a prescription and records dispensing details"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Prescription dispensing processed successfully",
                    content = @Content(schema = @Schema(implementation = DispenseRecordResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Prescription not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<DispenseRecordResponse> dispense(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dispense prescription payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = DispensePrescriptionRequest.class))
            )
            @RequestBody DispensePrescriptionRequest request
    ) {
        log.info("Received request to dispense prescription {}", request.prescriptionId());

        return AppApiResponse.success(
                "Prescription dispensing processed successfully",
                dispensingService.dispense(request)
        );
    }

    @GetMapping("/dispense-records/{id}")
    @Operation(
            summary = "Get dispense record by ID",
            description = "Fetch a dispensing record by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Dispense record retrieved successfully",
                    content = @Content(schema = @Schema(implementation = DispenseRecordResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Dispense record not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<DispenseRecordResponse> getById(@PathVariable UUID id) {
        return AppApiResponse.success(dispensingService.getById(id));
    }

    @GetMapping("/patients/{patientId}/dispense-records")
    @Operation(
            summary = "Get dispense records by patient",
            description = "Fetch all dispensing records for a specific patient"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Dispense records retrieved successfully",
                    content = @Content(schema = @Schema(implementation = DispenseRecordResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<List<DispenseRecordResponse>> getByPatient(
            @PathVariable UUID patientId
    ) {
        return AppApiResponse.success(dispensingService.getByPatient(patientId));
    }

    @GetMapping("/prescriptions/{prescriptionId}/dispense-records")
    @Operation(
            summary = "Get dispense records by prescription",
            description = "Fetch all dispensing records associated with a prescription"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Dispense records retrieved successfully",
                    content = @Content(schema = @Schema(implementation = DispenseRecordResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Prescription not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<List<DispenseRecordResponse>> getByPrescription(
            @PathVariable UUID prescriptionId
    ) {
        return AppApiResponse.success(dispensingService.getByPrescription(prescriptionId));
    }
}