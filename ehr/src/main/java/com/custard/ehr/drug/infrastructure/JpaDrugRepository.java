package com.custard.ehr.drug.infrastructure;

import com.custard.ehr.drug.application.ports.DrugRepository;
import com.custard.ehr.drug.domain.Drug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaDrugRepository extends JpaRepository<Drug, UUID>, DrugRepository {

    Optional<Drug> findByName(String name);

    List<Drug> findTop20ByNameContainingIgnoreCase(String name);
}