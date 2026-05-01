package com.custard.ehr.prescription.presentation;

import com.custard.ehr.prescription.application.dto.CreatePrescriptionRequest;
import com.custard.ehr.prescription.application.dto.PrescriptionResponse;
import com.custard.ehr.prescription.application.service.PrescriptionService;
import com.custard.ehr.shared.infrastruture.web.ApiResponse;
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
public class PrescriptionController {

    private final Logger log = LoggerFactory.getLogger(PrescriptionController.class);

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping
    public ApiResponse<PrescriptionResponse> create(
            @Valid @RequestBody CreatePrescriptionRequest request
    ) {
        log.info("Received request to create prescription for encounter {}", request.encounterId());

        return ApiResponse.success(
                "Prescription created and sent to pharmacy",
                prescriptionService.create(request)
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<PrescriptionResponse> getById(@PathVariable UUID id) {
        log.debug("Received request to fetch prescription ID: {}", id);
        return ApiResponse.success(prescriptionService.getById(id));
    }

    @GetMapping("/encounter/{encounterId}")
    public ApiResponse<List<PrescriptionResponse>> getByEncounter(
            @PathVariable UUID encounterId
    ) {
        log.debug("Received request to fetch prescriptions for encounter ID: {}", encounterId);
        return ApiResponse.success(prescriptionService.getByEncounter(encounterId));
    }

    @GetMapping("/patient/{patientId}")
    public ApiResponse<List<PrescriptionResponse>> getByPatient(
            @PathVariable UUID patientId
    ) {
        log.debug("Received request to fetch prescriptions for patient ID: {}", patientId);
        return ApiResponse.success(prescriptionService.getByPatient(patientId));
    }

    @GetMapping("/pharmacy-queue")
    public ApiResponse<List<PrescriptionResponse>> getPharmacyQueue() {
        log.debug("Received request to fetch pharmacy prescription queue");
        return ApiResponse.success(prescriptionService.getPharmacyQueue());
    }

    @PatchMapping("/{id}/cancel")
    public ApiResponse<PrescriptionResponse> cancel(@PathVariable UUID id) {
        log.info("Received request to cancel prescription ID: {}", id);

        return ApiResponse.success(
                "Prescription cancelled successfully",
                prescriptionService.cancel(id)
        );
    }
}