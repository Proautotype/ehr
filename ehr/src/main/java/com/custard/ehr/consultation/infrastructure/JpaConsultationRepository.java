package com.custard.ehr.consultation.infrastructure;

import com.custard.ehr.consultation.application.ports.ConsultationRepository;
import com.custard.ehr.consultation.domain.ConsultationNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaConsultationRepository
        extends JpaRepository<ConsultationNote, UUID>, ConsultationRepository {

    Optional<ConsultationNote> findTopByEncounterIdOrderByRecordedAtDesc(UUID encounterId);

    List<ConsultationNote> findByEncounterIdOrderByRecordedAtDesc(UUID encounterId);

    List<ConsultationNote> findByPatientIdOrderByRecordedAtDesc(UUID patientId);

    List<ConsultationNote> findByDoctorIdOrderByRecordedAtDesc(UUID doctorId);

    boolean existsByEncounterId(UUID encounterId);
}