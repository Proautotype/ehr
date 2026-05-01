package com.custard.ehr.patient.application.ports;

import com.custard.ehr.patient.domain.Patient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PatientRepository {

    Patient save(Patient patient);

    Optional<Patient> findById(UUID id);

    Optional<Patient> findByPatientNumberValue(String patientNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    List<Patient> findTop20ByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName,
            String lastName
    );
}