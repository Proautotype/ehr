package com.custard.ehr.laboratory.infrastructure;

import com.custard.ehr.laboratory.application.ports.LabTestRepository;
import com.custard.ehr.laboratory.domain.LabTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaLabTestRepository
        extends JpaRepository<LabTest, UUID>, LabTestRepository {

    boolean existsByNameIgnoreCase(String name);

    List<LabTest> findTop20ByNameContainingIgnoreCaseAndActiveTrue(String name);
}