package com.custard.ehr.consultation.presentation;

import com.custard.ehr.consultation.application.dto.ConsultationResponse;
import com.custard.ehr.consultation.application.dto.CreateConsultationRequest;
import com.custard.ehr.consultation.application.service.ConsultationService;
import com.custard.ehr.shared.infrastruture.web.ApiResponse;
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
public class ConsultationController {

    private final Logger log = LoggerFactory.getLogger(ConsultationController.class);

    private final ConsultationService consultationService;

    public ConsultationController(ConsultationService consultationService) {
        this.consultationService = consultationService;
    }

    @PostMapping
    public ApiResponse<ConsultationResponse> recordConsult(
            @Valid @RequestBody CreateConsultationRequest request
    ) {
        log.info("Received request to record consultation for encounter {}", request.encounterId());

        return ApiResponse.success(
                "Consultation recorded successfully",
                consultationService.recordConsult(request)
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<ConsultationResponse> getById(@PathVariable UUID id) {
        log.debug("Received request to fetch consultation ID: {}", id);
        return ApiResponse.success(consultationService.getById(id));
    }

    @GetMapping("/encounter/{encounterId}/latest")
    public ApiResponse<ConsultationResponse> getLatestByEncounter(
            @PathVariable UUID encounterId
    ) {
        log.debug("Received request to fetch latest consultation for encounter ID: {}", encounterId);
        return ApiResponse.success(consultationService.getLatestByEncounter(encounterId));
    }

    @GetMapping("/encounter/{encounterId}")
    public ApiResponse<List<ConsultationResponse>> getByEncounter(
            @PathVariable UUID encounterId
    ) {
        log.debug("Received request to fetch consultations for encounter ID: {}", encounterId);
        return ApiResponse.success(consultationService.getByEncounter(encounterId));
    }

    @GetMapping("/patient/{patientId}")
    public ApiResponse<List<ConsultationResponse>> getByPatient(
            @PathVariable UUID patientId
    ) {
        log.debug("Received request to fetch consultations for patient ID: {}", patientId);
        return ApiResponse.success(consultationService.getByPatient(patientId));
    }

    @GetMapping("/doctor/{doctorId}")
    public ApiResponse<List<ConsultationResponse>> getByDoctor(
            @PathVariable UUID doctorId
    ) {
        log.debug("Received request to fetch consultations for doctor ID: {}", doctorId);
        return ApiResponse.success(consultationService.getByDoctor(doctorId));
    }
}