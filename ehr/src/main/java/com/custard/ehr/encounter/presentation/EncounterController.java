package com.custard.ehr.encounter.presentation;

import com.custard.ehr.encounter.application.dto.CreateEncounterRequest;
import com.custard.ehr.encounter.application.dto.EncounterResponse;
import com.custard.ehr.encounter.application.service.EncounterService;
import com.custard.ehr.shared.infrastruture.web.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/encounters")
public class EncounterController {

    private static final Logger log = LoggerFactory.getLogger(EncounterController.class);
    private final EncounterService encounterService;

    public EncounterController(EncounterService encounterService) {
        this.encounterService = encounterService;
    }

    @PostMapping
    public ApiResponse<EncounterResponse> create(
            @Valid @RequestBody CreateEncounterRequest request
    ) {
        log.info("REST request to create encounter for patient: {}", request.patientId());
        return ApiResponse.success(
                "Encounter created successfully",
                encounterService.create(request)
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<EncounterResponse> getById(@PathVariable UUID id) {
        log.debug("REST request to get encounter: {}", id);
        return ApiResponse.success(encounterService.getById(id));
    }

    @GetMapping("/patient/{patientId}")
    public ApiResponse<List<EncounterResponse>> getByPatient(
            @PathVariable UUID patientId
    ) {
        log.debug("REST request to get encounters for patient: {}", patientId);
        return ApiResponse.success(encounterService.getByPatient(patientId));
    }

    @GetMapping("/active")
    public ApiResponse<List<EncounterResponse>> getActiveEncounters() {
        log.debug("REST request to get all active encounters");
        return ApiResponse.success(encounterService.getActiveEncounters());
    }

    @PatchMapping("/{id}/complete")
    public ApiResponse<EncounterResponse> complete(@PathVariable UUID id) {
        log.info("REST request to complete encounter: {}", id);
        return ApiResponse.success(
                "Encounter completed successfully",
                encounterService.complete(id)
        );
    }

    @PatchMapping("/{id}/cancel")
    public ApiResponse<EncounterResponse> cancel(@PathVariable UUID id) {
        log.info("REST request to cancel encounter: {}", id);
        return ApiResponse.success(
                "Encounter cancelled successfully",
                encounterService.cancel(id)
        );
    }

    @PatchMapping("/{id}/payment/paid")
    public ApiResponse<EncounterResponse> markPaymentPaid(@PathVariable UUID id) {
        log.info("REST request to mark payment as PAID for encounter: {}", id);
        return ApiResponse.success(encounterService.markPaymentPaid(id));
    }

    @PatchMapping("/{id}/payment/waive")
    public ApiResponse<EncounterResponse> waivePayment(@PathVariable UUID id) {
        log.info("REST request to waive payment for encounter: {}", id);
        return ApiResponse.success(encounterService.waivePayment(id));
    }

    @PatchMapping("/{id}/lab/pending")
    public ApiResponse<EncounterResponse> markLabPending(@PathVariable UUID id) {
        log.debug("REST request to set lab status to PENDING for encounter: {}", id);
        return ApiResponse.success(encounterService.markLabPending(id));
    }

    @PatchMapping("/{id}/lab/completed")
    public ApiResponse<EncounterResponse> markLabCompleted(@PathVariable UUID id) {
        log.info("REST request to set lab status to COMPLETED for encounter: {}", id);
        return ApiResponse.success(encounterService.markLabCompleted(id));
    }

    @PatchMapping("/{id}/pharmacy/pending")
    public ApiResponse<EncounterResponse> markPharmacyPending(@PathVariable UUID id) {
        log.debug("REST request to set pharmacy status to PENDING for encounter: {}", id);
        return ApiResponse.success(encounterService.markPharmacyPending(id));
    }

    @PatchMapping("/{id}/pharmacy/dispensed")
    public ApiResponse<EncounterResponse> markPharmacyDispensed(@PathVariable UUID id) {
        log.info("REST request to set pharmacy status to DISPENSED for encounter: {}", id);
        return ApiResponse.success(encounterService.markPharmacyDispensed(id));
    }
}