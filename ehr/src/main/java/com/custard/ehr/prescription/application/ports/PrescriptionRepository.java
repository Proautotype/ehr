package com.custard.ehr.prescription.application.ports;

import com.custard.ehr.prescription.domain.Prescription;
import com.custard.ehr.prescription.domain.PrescriptionStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PrescriptionRepository {

    Prescription save(Prescription prescription);

    Optional<Prescription> findById(UUID id);

    List<Prescription> findByEncounterIdOrderByPrescribedAtDesc(UUID encounterId);

    List<Prescription> findByPatientIdOrderByPrescribedAtDesc(UUID patientId);

    List<Prescription> findByStatusOrderByPrescribedAtAsc(PrescriptionStatus status);
}