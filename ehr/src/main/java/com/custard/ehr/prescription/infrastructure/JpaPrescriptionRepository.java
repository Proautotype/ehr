package com.custard.ehr.prescription.infrastructure;

import com.custard.ehr.prescription.application.ports.PrescriptionRepository;
import com.custard.ehr.prescription.domain.Prescription;
import com.custard.ehr.prescription.domain.PrescriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaPrescriptionRepository
        extends JpaRepository<Prescription, UUID>, PrescriptionRepository {

    List<Prescription> findByEncounterIdOrderByPrescribedAtDesc(UUID encounterId);

    List<Prescription> findByPatientIdOrderByPrescribedAtDesc(UUID patientId);

    List<Prescription> findByStatusOrderByPrescribedAtAsc(PrescriptionStatus status);
}