package com.custard.ehr.laboratory.application.dto.external;

import com.custard.ehr.laboratory.domain.LabOrderStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import java.util.Objects;

public class LabOrderPatientProjectionResponse {
    private UUID id;
    private UUID encounterId;
    private UUID patientId;
    private UUID orderedBy;
    private Instant orderedAt;
    private LabOrderStatus status;
    private String clinicalNote;

    private String patientNumber;
    private String fullName;
    private LocalDate dateOfBirth;

    public LabOrderPatientProjectionResponse(
            UUID id, UUID encounterId, UUID patientId,
            UUID orderedBy, Instant orderedAt, LabOrderStatus status,
            String clinicalNote, String fullName,
            LocalDate dateOfBirth
    ) {
        this.id = id;
        this.encounterId = encounterId;
        this.patientId = patientId;
        this.orderedBy = orderedBy;
        this.orderedAt = orderedAt;
        this.status = status;
        this.clinicalNote = clinicalNote;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.patientNumber = patientNumber;
    }

    public LabOrderPatientProjectionResponse(UUID id, UUID encounterId, UUID patientId, UUID orderedBy, Instant orderedAt, LabOrderStatus status, String clinicalNote, String patientNumber, String fullName, LocalDate dateOfBirth) {
        this.id = id;
        this.encounterId = encounterId;
        this.patientId = patientId;
        this.orderedBy = orderedBy;
        this.orderedAt = orderedAt;
        this.status = status;
        this.clinicalNote = clinicalNote;
        this.patientNumber = patientNumber;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
    }

//    public LabOrderPatientProjectionResponse(Object... args) {
//        System.out.println("Constructor called with " + args.length + " args");
//        for (int i = 0; i < args.length; i++) {
//            System.out.println("Arg " + i + ": " + args[i] + " (type: " +
//                    (args[i] != null ? args[i].getClass().getSimpleName() : "null") + ")");
//        }
//        // Your actual initialization logic
//        this.id = (UUID) args[0];
//        this.encounterId = (UUID) args[1];
//        // ... map all fields
//    }

    public UUID getId() {
        return id;
    }

    public UUID getEncounterId() {
        return encounterId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public UUID getOrderedBy() {
        return orderedBy;
    }

    public Instant getOrderedAt() {
        return orderedAt;
    }

    public LabOrderStatus getStatus() {
        return status;
    }

    public String getClinicalNote() {
        return clinicalNote;
    }

    public String getPatientNumber() {
        return patientNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LabOrderPatientProjectionResponse that = (LabOrderPatientProjectionResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(encounterId, that.encounterId) && Objects.equals(patientId, that.patientId) && Objects.equals(orderedBy, that.orderedBy) && Objects.equals(orderedAt, that.orderedAt) && status == that.status && Objects.equals(clinicalNote, that.clinicalNote) && Objects.equals(patientNumber, that.patientNumber) && Objects.equals(fullName, that.fullName) && Objects.equals(dateOfBirth, that.dateOfBirth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, encounterId, patientId, orderedBy, orderedAt, status, clinicalNote, patientNumber, fullName, dateOfBirth);
    }

    @Override
    public String toString() {
        return "LabOrderPatientProjectionResponse{" +
                "id=" + id +
                ", encounterId=" + encounterId +
                ", patientId=" + patientId +
                ", orderedBy=" + orderedBy +
                ", orderedAt=" + orderedAt +
                ", status=" + status +
                ", clinicalNote='" + clinicalNote + '\'' +
                ", patientNumber='" + patientNumber + '\'' +
                ", fullName='" + fullName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }
}