package com.custard.ehr.laboratory.application.ports;

import com.custard.ehr.laboratory.domain.LabTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LabTestRepository {

    LabTest save(LabTest labTest);

    Optional<LabTest> findById(UUID id);

    boolean existsByNameIgnoreCase(String name);

    List<LabTest> findTop20ByNameContainingIgnoreCaseAndActiveTrue(String name);
}