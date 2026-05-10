package com.custard.ehr.patient.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdatePatientRequest(
        @JsonProperty(value = "firstName")
        String firstName,
        @JsonProperty(value = "lastName")
        String lastName,
        @JsonProperty(value = "dateOfBirth")
        String dateOfBirth,
        @JsonProperty(value = "gender")
        String gender,
        @JsonProperty(value = "address")
        String address,
        @JsonProperty(value = "email")
        String email,
        @JsonProperty(value = "phoneNumber")
        String phoneNumber,
        @JsonProperty(value = "active")
        Boolean active
) {
    @Override
    public String toString() {
        return "UpdatePatientRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", gender='" + gender + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", active='" + active + '\'' +
                '}';
    }
}
