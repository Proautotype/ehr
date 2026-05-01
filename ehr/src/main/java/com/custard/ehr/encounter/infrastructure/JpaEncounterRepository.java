package com.custard.ehr.encounter.infrastructure;

import com.custard.ehr.encounter.application.ports.EncounterRepository;
import com.custard.ehr.encounter.domain.Encounter;
import com.custard.ehr.encounter.domain.EncounterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaEncounterRepository
        extends JpaRepository<Encounter, UUID>, EncounterRepository {

    List<Encounter> findByPatientIdOrderByOpenedAtDesc(UUID patientId);

    List<Encounter> findByStatusOrderByOpenedAtDesc(EncounterStatus status);

    boolean existsByPatientIdAndStatus(UUID patientId, EncounterStatus status);
}