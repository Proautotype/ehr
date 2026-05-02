package com.custard.ehr.consultation.presentation;

import com.custard.ehr.consultation.application.dto.ConsultationResponse;
import com.custard.ehr.consultation.application.dto.CreateConsultationRequest;
import com.custard.ehr.consultation.application.service.ConsultationService;
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
@RequestMapping("/api/v1/consultations")
@Slf4j
@Tag(name = "Consultations", description = "Consultation and clinical interaction management APIs")
@SecurityRequirement(name = "bearerAuth")
public class ConsultationController {

    private final Logger log = LoggerFactory.getLogger(ConsultationController.class);

    private final ConsultationService consultationService;

    public ConsultationController(ConsultationService consultationService) {
        this.consultationService = consultationService;
    }

    @PostMapping
    @Operation(
            summary = "Record consultation",
            description = "Records a new consultation for a given encounter"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Consultation recorded successfully",
                    content = @Content(schema = @Schema(implementation = ConsultationResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Encounter not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<ConsultationResponse> recordConsult(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Consultation creation payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateConsultationRequest.class))
            )
            @RequestBody CreateConsultationRequest request
    ) {
        log.info("Received request to record consultation for encounter {}", request.encounterId());

        return AppApiResponse.success(
                "Consultation recorded successfully",
                consultationService.recordConsult(request)
        );
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get consultation by ID",
            description = "Fetch a consultation by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Consultation retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ConsultationResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Consultation not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<ConsultationResponse> getById(@PathVariable UUID id) {
        log.debug("Received request to fetch consultation ID: {}", id);
        return AppApiResponse.success(consultationService.getById(id));
    }

    @GetMapping("/encounter/{encounterId}/latest")
    @Operation(
            summary = "Get latest consultation by encounter",
            description = "Fetch the most recent consultation for a specific encounter"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Latest consultation retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ConsultationResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Encounter or consultation not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<ConsultationResponse> getLatestByEncounter(
            @PathVariable UUID encounterId
    ) {
        log.debug("Received request to fetch latest consultation for encounter ID: {}", encounterId);
        return AppApiResponse.success(consultationService.getLatestByEncounter(encounterId));
    }

    @GetMapping("/encounter/{encounterId}")
    @Operation(
            summary = "Get consultations by encounter",
            description = "Fetch all consultations associated with a specific encounter"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Consultations retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ConsultationResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Encounter not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<List<ConsultationResponse>> getByEncounter(
            @PathVariable UUID encounterId
    ) {
        log.debug("Received request to fetch consultations for encounter ID: {}", encounterId);
        return AppApiResponse.success(consultationService.getByEncounter(encounterId));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(
            summary = "Get consultations by patient",
            description = "Fetch all consultations for a specific patient"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Consultations retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ConsultationResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<List<ConsultationResponse>> getByPatient(
            @PathVariable UUID patientId
    ) {
        log.debug("Received request to fetch consultations for patient ID: {}", patientId);
        return AppApiResponse.success(consultationService.getByPatient(patientId));
    }

    @GetMapping("/doctor/{doctorId}")
    @Operation(
            summary = "Get consultations by doctor",
            description = "Fetch all consultations performed by a specific doctor"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Consultations retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ConsultationResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Doctor not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<List<ConsultationResponse>> getByDoctor(
            @PathVariable UUID doctorId
    ) {
        log.debug("Received request to fetch consultations for doctor ID: {}", doctorId);
        return AppApiResponse.success(consultationService.getByDoctor(doctorId));
    }
}