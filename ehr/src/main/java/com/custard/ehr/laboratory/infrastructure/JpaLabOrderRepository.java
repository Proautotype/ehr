package com.custard.ehr.laboratory.infrastructure;

import com.custard.ehr.laboratory.application.ports.LabOrderRepository;
import com.custard.ehr.laboratory.domain.LabOrder;
import com.custard.ehr.laboratory.domain.LabOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaLabOrderRepository
        extends JpaRepository<LabOrder, UUID>, LabOrderRepository {

    List<LabOrder> findByEncounterIdOrderByOrderedAtDesc(UUID encounterId);

    List<LabOrder> findByPatientIdOrderByOrderedAtDesc(UUID patientId);

    List<LabOrder> findByStatusOrderByOrderedAtAsc(LabOrderStatus status);
}