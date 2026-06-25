package com.custard.ehr.drug.infrastructure;

import com.custard.ehr.drug.application.ports.DrugRepository;
import com.custard.ehr.drug.domain.Drug;
import jakarta.persistence.Tuple;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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


    Optional<Drug> findByCode(String code);

    @Query("SELECT d FROM Drug d WHERE " +
            "(:query IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(d.code) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(d.description) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
            "(:form IS NULL OR LOWER(d.form) = LOWER(:form)) AND " +
            "(:unit IS NULL OR LOWER(d.unit) = LOWER(:unit)) AND " +
            "(:active IS NULL OR d.active = :active)")
    Page<Drug> searchDrugs(@Param("query") String query,
                           @Param("form") String form,
                           @Param("unit") String unit,
                           @Param("active") Boolean active,
                           Pageable pageable);

    @Query("SELECT d FROM Drug d WHERE d.active = true")
    List<Drug> findAllActive();

    @Query("SELECT COUNT(d) FROM Drug d WHERE d.active = true")
    long countActive();

    @Query("SELECT COUNT(d) FROM Drug d WHERE d.active = false")
    long countInactive();

    @Override
    @Query("SELECT " +
            "p.id as id, " +
            "p.name as name, " +
            "COALESCE(SUM(si.quantity), 0) as quantity, " +
            "CASE " +
            "   WHEN COALESCE(SUM(si.quantity), 0) = 0 THEN 'OUT_OF_STOCK' " +
            "   WHEN COALESCE(SUM(si.quantity), 0) > 0 AND COALESCE(SUM(si.quantity), 0) < 10 THEN 'LOW_STOCK' " +
            "   ELSE 'IN_STOCK' " +
            "END as status " +
            "FROM Drug p " +
            "LEFT JOIN p.stockItems si " +
            "WHERE :spec IS NULL OR :spec IS NOT NULL " +
            "GROUP BY p.id, p.name " +
            "HAVING " +
            "   (:stockStatus = 'ALL' OR " +
            "    (:stockStatus = 'IN_STOCK' AND COALESCE(SUM(si.quantity), 0) >= 10) OR " +
            "    (:stockStatus = 'LOW_STOCK' AND COALESCE(SUM(si.quantity), 0) > 0 AND COALESCE(SUM(si.quantity), 0) < 10) OR " +
            "    (:stockStatus = 'OUT_OF_STOCK' AND COALESCE(SUM(si.quantity), 0) = 0))")
    Page<Tuple> findProductsByStockWithSpecificationAsTuple(
            Specification<Drug> spec,
            String stockStatus,
            Pageable pageable
    );
}
