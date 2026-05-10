package com.custard.ehr.patient.application.dto;

public record UpdatePatientRequestRecord(
        String firstName,
        String lastName,
        String dateOfBirth,
        String gender,
        String address,
        String email,
        String phoneNumber
) {
}
