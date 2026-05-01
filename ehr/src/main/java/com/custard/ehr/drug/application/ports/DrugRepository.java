package com.custard.ehr.drug.application.ports;

import com.custard.ehr.drug.domain.Drug;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DrugRepository {

    Drug save(Drug drug);

    Optional<Drug> findById(UUID id);

    Optional<Drug> findByName(String name);

    List<Drug> findTop20ByNameContainingIgnoreCase(String name);
}