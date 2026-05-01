package com.custard.ehr.vitals.infrastructure;

import com.custard.ehr.vitals.application.ports.VitalsRepository;
import com.custard.ehr.vitals.domain.Vitals;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaVitalsRepository extends JpaRepository<Vitals, UUID>, VitalsRepository {

    Optional<Vitals> findTopByEncounterIdOrderByRecordedAtDesc(UUID encounterId);

    List<Vitals> findByEncounterIdOrderByRecordedAtDesc(UUID encounterId);

    List<Vitals> findByPatientIdOrderByRecordedAtDesc(UUID patientId);

    boolean existsByEncounterId(UUID encounterId);
}