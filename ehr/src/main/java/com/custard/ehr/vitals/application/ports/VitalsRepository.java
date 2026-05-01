package com.custard.ehr.vitals.application.ports;


import com.custard.ehr.vitals.domain.Vitals;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VitalsRepository {

    Vitals save(Vitals vitals);

    Optional<Vitals> findById(UUID id);

    Optional<Vitals> findTopByEncounterIdOrderByRecordedAtDesc(UUID encounterId);

    List<Vitals> findByEncounterIdOrderByRecordedAtDesc(UUID encounterId);

    List<Vitals> findByPatientIdOrderByRecordedAtDesc(UUID patientId);

    boolean existsByEncounterId(UUID encounterId);
}