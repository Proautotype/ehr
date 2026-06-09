package com.custard.ehr.laboratory.application.dto.external;

import java.time.LocalDate;
import java.util.Objects;

public class PatientInfo {
    private final String patientNumber;
    private final String fullName;
    private final LocalDate dateOfBirth;

    public PatientInfo(String patientNumber, String fullName, LocalDate dateOfBirth) {
        this.patientNumber = patientNumber;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatientInfo that = (PatientInfo) o;
        return Objects.equals(patientNumber, that.patientNumber) &&
                Objects.equals(fullName, that.fullName) &&
                Objects.equals(dateOfBirth, that.dateOfBirth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientNumber, fullName, dateOfBirth);
    }

    @Override
    public String toString() {
        return "PatientInfo{" +
                "patientNumber='" + patientNumber + '\'' +
                ", fullName='" + fullName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }
}