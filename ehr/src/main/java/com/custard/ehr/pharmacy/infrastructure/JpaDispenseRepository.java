package com.custard.ehr.pharmacy.infrastructure;

import com.custard.ehr.pharmacy.application.ports.DispenseRepository;
import com.custard.ehr.pharmacy.domain.DispenseRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaDispenseRepository
        extends JpaRepository<DispenseRecord, UUID>, DispenseRepository {

    List<DispenseRecord> findByPatientIdOrderByDispensedAtDesc(UUID patientId);

    List<DispenseRecord> findByPrescriptionIdOrderByDispensedAtDesc(UUID prescriptionId);

    boolean existsByPrescriptionId(UUID prescriptionId);
}