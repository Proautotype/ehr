package com.custard.ehr.consultation.application.ports;

import com.custard.ehr.consultation.domain.ConsultationNote;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsultationRepository {

    ConsultationNote save(ConsultationNote consultationNote);

    Optional<ConsultationNote> findById(UUID id);

    Optional<ConsultationNote> findTopByEncounterIdOrderByRecordedAtDesc(UUID encounterId);

    List<ConsultationNote> findByEncounterIdOrderByRecordedAtDesc(UUID encounterId);

    List<ConsultationNote> findByPatientIdOrderByRecordedAtDesc(UUID patientId);

    List<ConsultationNote> findByDoctorIdOrderByRecordedAtDesc(UUID doctorId);

    boolean existsByEncounterId(UUID encounterId);
}