package com.custard.ehr.patient.application.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record RegisterPatientRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        LocalDate dateOfBirth,
        String gender,
        String phoneNumber,
        String email,
        String address
) {
}