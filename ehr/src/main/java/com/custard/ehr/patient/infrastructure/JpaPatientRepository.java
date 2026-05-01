package com.custard.ehr.patient.infrastructure;

import com.custard.ehr.patient.application.ports.PatientRepository;
import com.custard.ehr.patient.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaPatientRepository
        extends JpaRepository<Patient, UUID>, PatientRepository {
}