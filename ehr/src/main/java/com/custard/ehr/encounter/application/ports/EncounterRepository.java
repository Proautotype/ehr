package com.custard.ehr.encounter.application.ports;

import com.custard.ehr.encounter.domain.Encounter;
import com.custard.ehr.encounter.domain.EncounterStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EncounterRepository {

    Encounter save(Encounter encounter);

    Optional<Encounter> findById(UUID id);

    List<Encounter> findByPatientIdOrderByOpenedAtDesc(UUID patientId);

    List<Encounter> findByStatusOrderByOpenedAtDesc(EncounterStatus status);

    boolean existsByPatientIdAndStatus(UUID patientId, EncounterStatus status);
}