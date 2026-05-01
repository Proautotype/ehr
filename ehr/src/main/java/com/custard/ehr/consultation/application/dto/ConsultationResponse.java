package com.custard.ehr.consultation.application.dto;

import com.custard.ehr.consultation.domain.ConsultationNote;

import java.time.Instant;
import java.util.UUID;

public record ConsultationResponse(
        UUID id,
        UUID encounterId,
        UUID patientId,
        UUID doctorId,
        Instant recordedAt,
        String symptoms,
        String diagnosis,
        String clinicalNotes,
        String treatmentPlan,
        String followUpInstructions
) {
    public static ConsultationResponse from(ConsultationNote note) {
        return new ConsultationResponse(
                note.getId(),
                note.getEncounterId(),
                note.getPatientId(),
                note.getDoctorId(),
                note.getRecordedAt(),
                note.getSymptoms(),
                note.getDiagnosis(),
                note.getClinicalNotes(),
                note.getTreatmentPlan(),
                note.getFollowUpInstructions()
        );
    }
}