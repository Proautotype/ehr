package com.custard.ehr.vitals.presentation;

import com.custard.ehr.shared.infrastruture.web.ApiResponse;
import com.custard.ehr.vitals.application.dto.RecordVitalsRequest;
import com.custard.ehr.vitals.application.dto.VitalsResponse;
import com.custard.ehr.vitals.application.service.VitalsService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vitals")
@Slf4j
public class VitalsController {

    private final Logger log  = LoggerFactory.getLogger(VitalsController.class);
    private final VitalsService vitalsService;

    public VitalsController(VitalsService vitalsService) {
        this.vitalsService = vitalsService;
    }

    @PostMapping
    public ApiResponse<VitalsResponse> recordVitals(
            @Valid @RequestBody RecordVitalsRequest request
    ) {
        log.info(
                "Received request to record vitals for encounter {}",
                request.encounterId()
        );

        return ApiResponse.success(
                "Vitals recorded successfully",
                vitalsService.recordVitals(request)
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<VitalsResponse> getById(@PathVariable UUID id) {
        log.debug("Received request to fetch vitals ID: {}", id);
        return ApiResponse.success(vitalsService.getById(id));
    }

    @GetMapping("/encounter/{encounterId}/latest")
    public ApiResponse<VitalsResponse> getLatestByEncounter(
            @PathVariable UUID encounterId
    ) {
        log.debug("Received request to fetch latest vitals for encounter ID: {}", encounterId);
        return ApiResponse.success(vitalsService.getLatestByEncounter(encounterId));
    }

    @GetMapping("/encounter/{encounterId}")
    public ApiResponse<List<VitalsResponse>> getByEncounter(
            @PathVariable UUID encounterId
    ) {
        log.debug("Received request to fetch vitals for encounter ID: {}", encounterId);
        return ApiResponse.success(vitalsService.getByEncounter(encounterId));
    }

    @GetMapping("/patient/{patientId}")
    public ApiResponse<List<VitalsResponse>> getByPatient(
            @PathVariable UUID patientId
    ) {
        log.debug("Received request to fetch vitals history for patient ID: {}", patientId);
        return ApiResponse.success(vitalsService.getByPatient(patientId));
    }
}