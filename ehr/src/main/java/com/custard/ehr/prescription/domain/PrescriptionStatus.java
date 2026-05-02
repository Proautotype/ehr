package com.custard.ehr.prescription.domain;

public enum PrescriptionStatus {
    CREATED,
    SENT_TO_PHARMACY,
    PARTIALLY_DISPENSED,
    DISPENSED,
    CANCELLED
}