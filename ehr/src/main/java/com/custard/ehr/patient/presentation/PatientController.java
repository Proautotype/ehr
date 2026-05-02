package com.custard.ehr.patient.presentation;

import com.custard.ehr.patient.application.service.PatientService;
import com.custard.ehr.patient.application.dto.AddAllergyRequest;
import com.custard.ehr.patient.application.dto.PatientResponse;
import com.custard.ehr.patient.application.dto.RegisterPatientRequest;
import com.custard.ehr.shared.infrastruture.web.AppApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patients")
@Tag(name = "Patients", description = "Patient registration and management APIs")
@SecurityRequirement(name = "bearerAuth")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    @Operation(
            summary = "Register patient",
            description = "Registers a new patient in the system"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Patient registered successfully",
                    content = @Content(schema = @Schema(implementation = PatientResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<PatientResponse> register(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Patient registration payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegisterPatientRequest.class))
            )
            @RequestBody RegisterPatientRequest request
    ) {
        return AppApiResponse.success(
                "Patient registered successfully",
                patientService.register(request)
        );
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get patient by ID",
            description = "Fetch a patient by their unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Patient retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PatientResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<PatientResponse> getById(@PathVariable UUID id) {
        return AppApiResponse.success(patientService.getById(id));
    }

    @GetMapping("/number/{patientNumber}")
    @Operation(
            summary = "Get patient by patient number",
            description = "Fetch a patient using their unique patient number"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Patient retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PatientResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<PatientResponse> getByPatientNumber(
            @PathVariable String patientNumber
    ) {
        return AppApiResponse.success(patientService.getByPatientNumber(patientNumber));
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search patients",
            description = "Search patients by name, number, or keyword"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Search results retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PatientResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid search query"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<List<PatientResponse>> search(
            @RequestParam String query
    ) {
        return AppApiResponse.success(patientService.search(query));
    }

    @PostMapping("/{patientId}/allergies")
    @Operation(
            summary = "Add patient allergy",
            description = "Adds an allergy record to a patient profile"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Allergy added successfully",
                    content = @Content(schema = @Schema(implementation = PatientResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<PatientResponse> addAllergy(
            @PathVariable UUID patientId,
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Allergy payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AddAllergyRequest.class))
            )
            @RequestBody AddAllergyRequest request
    ) {
        return AppApiResponse.success(
                "Allergy added successfully",
                patientService.addAllergy(patientId, request)
        );
    }
}