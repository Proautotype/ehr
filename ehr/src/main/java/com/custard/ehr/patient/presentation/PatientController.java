package com.custard.ehr.patient.presentation;

import com.custard.ehr.patient.application.service.PatientService;
import com.custard.ehr.patient.application.dto.AddAllergyRequest;
import com.custard.ehr.patient.application.dto.PatientResponse;
import com.custard.ehr.patient.application.dto.RegisterPatientRequest;
import com.custard.ehr.shared.infrastruture.web.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    public ApiResponse<PatientResponse> register(
            @Valid @RequestBody RegisterPatientRequest request
    ) {
        return ApiResponse.success(
                "Patient registered successfully",
                patientService.register(request)
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<PatientResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(patientService.getById(id));
    }

    @GetMapping("/number/{patientNumber}")
    public ApiResponse<PatientResponse> getByPatientNumber(
            @PathVariable String patientNumber
    ) {
        return ApiResponse.success(patientService.getByPatientNumber(patientNumber));
    }

    @GetMapping("/search")
    public ApiResponse<List<PatientResponse>> search(
            @RequestParam String query
    ) {
        return ApiResponse.success(patientService.search(query));
    }

    @PostMapping("/{patientId}/allergies")
    public ApiResponse<PatientResponse> addAllergy(
            @PathVariable UUID patientId,
            @Valid @RequestBody AddAllergyRequest request
    ) {
        return ApiResponse.success(
                "Allergy added successfully",
                patientService.addAllergy(patientId, request)
        );
    }
}