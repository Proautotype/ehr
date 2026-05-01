package com.custard.ehr.consultation.domain;

import com.custard.ehr.shared.domain.AuditableEntity;
import com.custard.ehr.shared.exception.BusinessException;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "consultation_notes",
        indexes = {
                @Index(name = "idx_consultation_encounter", columnList = "encounterId"),
                @Index(name = "idx_consultation_patient", columnList = "patientId"),
                @Index(name = "idx_consultation_doctor", columnList = "doctorId"),
                @Index(name = "idx_consultation_recorded_at", columnList = "recordedAt")
        }
)
public class ConsultationNote extends AuditableEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @Column(nullable = false)
    private UUID encounterId;

    @Column(nullable = false)
    private UUID patientId;

    @Column(nullable = false)
    private UUID doctorId;

    @Column(nullable = false)
    private Instant recordedAt;

    @Column(length = 3000)
    private String symptoms;

    @Column(length = 3000)
    private String diagnosis;

    @Column(length = 5000)
    private String clinicalNotes;

    @Column(length = 3000)
    private String treatmentPlan;

    @Column(length = 2000)
    private String followUpInstructions;

    protected ConsultationNote() {
    }

    public ConsultationNote(
            UUID encounterId,
            UUID patientId,
            UUID doctorId,
            String symptoms,
            String diagnosis,
            String clinicalNotes,
            String treatmentPlan,
            String followUpInstructions
    ) {
        if (isBlank(symptoms) && isBlank(diagnosis) && isBlank(clinicalNotes)) {
            throw new BusinessException("Consultation must contain symptoms, diagnosis, or clinical notes");
        }

        this.encounterId = encounterId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.symptoms = symptoms;
        this.diagnosis = diagnosis;
        this.clinicalNotes = clinicalNotes;
        this.treatmentPlan = treatmentPlan;
        this.followUpInstructions = followUpInstructions;
        this.recordedAt = Instant.now();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public UUID getId() {
        return id;
    }

    public UUID getEncounterId() {
        return encounterId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public UUID getDoctorId() {
        return doctorId;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getClinicalNotes() {
        return clinicalNotes;
    }

    public String getTreatmentPlan() {
        return treatmentPlan;
    }

    public String getFollowUpInstructions() {
        return followUpInstructions;
    }
}