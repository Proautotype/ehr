package com.custard.ehr.laboratory.application.ports;

import com.custard.ehr.laboratory.domain.LabOrder;
import com.custard.ehr.laboratory.domain.LabOrderStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LabOrderRepository {

    LabOrder save(LabOrder order);

    Optional<LabOrder> findById(UUID id);

    List<LabOrder> findByEncounterIdOrderByOrderedAtDesc(UUID encounterId);

    List<LabOrder> findByPatientIdOrderByOrderedAtDesc(UUID patientId);

    List<LabOrder> findByStatusOrderByOrderedAtAsc(LabOrderStatus status);
}