package com.custard.ehr.patient.application.mapper;

import com.custard.ehr.patient.application.dto.UpdatePatientRequest;
import com.custard.ehr.patient.domain.Patient;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
@Component
public class PatientMapper {

    public void updatePatientFromRequest(UpdatePatientRequest request, Patient patient) {


        if (request == null || patient == null) {
            return;
        }

        if (validField(request.firstName())) {
            patient.setFirstName(request.firstName().trim());
        }

        if (validField(request.lastName())) {
            patient.setLastName(request.lastName().trim());
        }

        if (validField(request.gender())) {
            patient.setGender(request.gender().trim());
        }

        if (validField(request.address())) {
            patient.setAddress(request.address().trim());
        }

        if (validField(request.email())) {
            patient.setEmail(request.email().trim());
        }

        if (validField(request.phoneNumber())) {
            patient.setPhoneNumber(request.phoneNumber().trim());
        }

        if (validField(request.dateOfBirth())) {
            try {
                // Parse the ISO 8601 string and convert to LocalDate
                ZonedDateTime zdt = ZonedDateTime.parse(request.dateOfBirth().trim());
                patient.setDateOfBirth(zdt.toLocalDate());
            } catch (DateTimeParseException e) {
                // Fallback: Try parsing as a simple date if the first one fails
                try {
                    patient.setDateOfBirth(LocalDate.parse(request.dateOfBirth().trim()));
                } catch (DateTimeParseException e2) {
                    throw new IllegalArgumentException("Invalid date format. Expected yyyy-MM-dd or ISO 8601 timestamp");
                }
            }
        }

        if (request.active() != null) {
            patient.setActive(request.active());
        }
    }

    private boolean validField(String fieldValue) {
        return !fieldValue.isBlank();
    }
}