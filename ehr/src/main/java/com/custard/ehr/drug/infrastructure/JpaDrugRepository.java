package com.custard.ehr.drug.infrastructure;

import com.custard.ehr.drug.application.ports.DrugRepository;
import com.custard.ehr.drug.domain.Drug;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaDrugRepository extends JpaRepository<Drug, UUID>, DrugRepository, JpaSpecificationExecutor<Drug> {

    Optional<Drug> findByName(String name);

    List<Drug> findTop20ByNameContainingIgnoreCase(String name);

    @Override
    @NonNull
    Page<Drug> findAll(@NonNull Specification<Drug> specification, @NonNull Pageable pageable);

}
