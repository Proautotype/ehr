package com.custard.ehr.pharmacy.application.ports;

import com.custard.ehr.pharmacy.domain.DispenseRecord;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DispenseRepository {

    DispenseRecord save(DispenseRecord record);

    Optional<DispenseRecord> findById(UUID id);

    List<DispenseRecord> findByPatientIdOrderByDispensedAtDesc(UUID patientId);

    List<DispenseRecord> findByPrescriptionIdOrderByDispensedAtDesc(UUID prescriptionId);

    boolean existsByPrescriptionId(UUID prescriptionId);
}