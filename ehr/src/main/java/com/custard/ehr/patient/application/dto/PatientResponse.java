package com.custard.ehr.patient.application.dto;

import com.custard.ehr.patient.domain.Patient;

import java.time.LocalDate;
import java.util.UUID;

public record PatientResponse(
        UUID id,
        String patientNumber,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String gender,
        String phoneNumber,
        String email,
        String address,
        boolean active
) {
    public static PatientResponse from(Patient patient) {
        return new PatientResponse(
                patient.getId(),
                patient.getPatientNumber().getValue(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getDateOfBirth(),
                patient.getGender(),
                patient.getPhoneNumber(),
                patient.getEmail(),
                patient.getAddress(),
                patient.isActive()
        );
    }
}