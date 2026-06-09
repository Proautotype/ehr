package com.custard.ehr.laboratory.application.service;

import com.custard.ehr.encounter.domain.LabStatus;
import com.custard.ehr.laboratory.application.dto.external.LabOrderPatientProjectionResponse;
import com.custard.ehr.laboratory.domain.LabOrderStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class LabPatientService {

    private final Logger log = LoggerFactory.getLogger(LabPatientService.class);

    @PersistenceContext
    private EntityManager entityManager;

    private static final String LAB_ORDER_PATIENT_BY_ENCOUNTER_QUERY = """
                SELECT new com.custard.ehr.laboratory.application.dto.external.LabOrderPatientProjectionResponse(
                    o.id,
                    o.encounterId,
                    o.patientId,
                    o.orderedBy,
                    o.orderedAt,
                    o.status,
                    o.clinicalNote,
                    new com.custard.ehr.laboratory.application.dto.external.PatientInfo(
                        CONCAT(p.firstName, ' ', p.lastName),
                        p.dateOfBirth,
                        p.patientNumber
                    )
                )
                FROM LabOrder o
                JOIN Patient p ON p.id = o.patientId
                WHERE o.encounterId = :encounterId
            """;

    private static final String LAB_ORDER_PATIENT_BY_STATUS_QUERY = """
            SELECT new com.custard.ehr.laboratory.application.dto.external.LabOrderPatientProjectionResponse(
                o.id,
                o.encounterId,
                o.patientId,
                o.orderedBy,
                o.orderedAt,
                o.status,
                o.clinicalNote,
                CONCAT(p.firstName, ' ', p.lastName),
                p.dateOfBirth
            )
            FROM LabOrder o
            JOIN Patient p ON p.id = o.patientId
            WHERE o.status = :status
            """;

    public Optional<LabOrderPatientProjectionResponse> findByEncounter(UUID encounterId) {
        return entityManager
                .createQuery(LAB_ORDER_PATIENT_BY_ENCOUNTER_QUERY, LabOrderPatientProjectionResponse.class)
                .setParameter("encounterId", encounterId)
                .getResultStream()
                .findFirst();
    }

    public List<LabOrderPatientProjectionResponse> findByStatus(String status) {
        log.debug("Finding lab orders by status: {}", status);
        return entityManager
                .createQuery(LAB_ORDER_PATIENT_BY_STATUS_QUERY, LabOrderPatientProjectionResponse.class)
                .setParameter("status", LabOrderStatus.valueOf(status))
                .getResultList();
    }
}
