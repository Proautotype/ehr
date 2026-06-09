package com.custard.ehr.vitals.presentation;

import com.custard.ehr.shared.infrastruture.web.AppApiResponse;
import com.custard.ehr.vitals.application.dto.RecordVitalsRequest;
import com.custard.ehr.vitals.application.dto.UpdateVitalsRequest;
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

    private final Logger log = LoggerFactory.getLogger(VitalsController.class);
    private final VitalsService vitalsService;

    public VitalsController(VitalsService vitalsService) {
        this.vitalsService = vitalsService;
    }

    @PostMapping
    public AppApiResponse<VitalsResponse> recordVitals(
            @Valid @RequestBody RecordVitalsRequest request
    ) {
        log.info(
                "Received request to record vitals for encounter {}",
                request.encounterId()
        );

        return AppApiResponse.success(
                "Vitals recorded successfully",
                vitalsService.recordVitals(request)
        );
    }

    @PatchMapping("/{id}")
    public AppApiResponse<Boolean> update(
            @PathVariable("id") UUID encounterId,
            @Valid @RequestBody UpdateVitalsRequest request
    ) {
        log.info("update vitals by id {}", encounterId);

        var updateRecord = vitalsService.updateRecord(encounterId, request);

        return AppApiResponse.success("Vitals recorded successfully", updateRecord);
    }

    @GetMapping("/{id}")
    public AppApiResponse<VitalsResponse> getById(@PathVariable UUID id) {
        log.debug("Received request to fetch vitals ID: {}", id);
        return AppApiResponse.success(vitalsService.getById(id));
    }

    @GetMapping("/encounter/{encounterId}/latest")
    public AppApiResponse<VitalsResponse> getLatestByEncounter(
            @PathVariable UUID encounterId
    ) {
        log.debug("Received request to fetch latest vitals for encounter ID: {}", encounterId);
        return AppApiResponse.success(vitalsService.getLatestByEncounter(encounterId));
    }

    @GetMapping("/encounter/{encounterId}")
    public AppApiResponse<List<VitalsResponse>> getByEncounter(
            @PathVariable UUID encounterId
    ) {
        log.debug("Received request to fetch vitals for encounter ID: {}", encounterId);
        return AppApiResponse.success(vitalsService.getByEncounter(encounterId));
    }

    @GetMapping("/patient/{patientId}")
    public AppApiResponse<List<VitalsResponse>> getByPatient(
            @PathVariable UUID patientId
    ) {
        log.debug("Received request to fetch vitals history for patient ID: {}", patientId);
        return AppApiResponse.success(vitalsService.getByPatient(patientId));
    }
}