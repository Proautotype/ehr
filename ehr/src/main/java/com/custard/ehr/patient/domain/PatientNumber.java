package com.custard.ehr.patient.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED) // For JPA
public class PatientNumber {

    private String value;

    public PatientNumber(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Patient number is required");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "PatientNumber{" +
                "value='" + value + '\'' +
                '}';
    }
}