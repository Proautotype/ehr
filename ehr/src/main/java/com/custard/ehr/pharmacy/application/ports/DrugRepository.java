package com.custard.ehr.pharmacy.application.ports;

import com.custard.ehr.pharmacy.domain.Drug;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DrugRepository {

    Drug save(Drug drug);

    Optional<Drug> findById(UUID id);

    Page<Drug> findAll(Specification<Drug> specification, Pageable pageable);



    Optional<Drug> findByCode(String code);

    Page<Drug> searchDrugs(@Param("query") String query,
                           @Param("form") String form,
                           @Param("unit") String unit,
                           @Param("active") Boolean active,
                           Pageable pageable);

    List<Drug> findAllActive();

    long countActive();

    long countInactive();

    Page<Tuple> findProductsByStockWithSpecificationAsTuple(
            Specification<Drug> spec,
            String stockStatus,
            Pageable pageable
    );

}