package com.custard.ehr.pharmacy.domain;

public enum NonDispenseReason {
    OUT_OF_STOCK,
    INSUFFICIENT_FUNDS,
    PATIENT_DECLINED,
    CLINICAL_REVIEW_REQUIRED,
    DRUG_SUBSTITUTED,
    OTHER
}