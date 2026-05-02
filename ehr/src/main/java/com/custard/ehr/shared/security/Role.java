package com.custard.ehr.shared.security;

import java.util.Set;

public enum Role {
    ADMIN(Set.of(Permission.values())),

    RECEPTIONIST(Set.of(
            Permission.PATIENT_REGISTER,
            Permission.PATIENT_VIEW,
            Permission.ENCOUNTER_CREATE,
            Permission.ENCOUNTER_VIEW
    )),

    NURSE(Set.of(
            Permission.PATIENT_VIEW,
            Permission.ENCOUNTER_VIEW,
            Permission.VITALS_RECORD,
            Permission.VITALS_VIEW
    )),

    DOCTOR(Set.of(
            Permission.PATIENT_VIEW,
            Permission.ENCOUNTER_VIEW,
            Permission.CONSULTATION_WRITE,
            Permission.CONSULTATION_VIEW,
            Permission.PRESCRIPTION_CREATE,
            Permission.PRESCRIPTION_VIEW,
            Permission.LAB_ORDER_CREATE,
            Permission.LAB_ORDER_VIEW
    )),

    LAB_TECHNICIAN(Set.of(
            Permission.PATIENT_VIEW,
            Permission.ENCOUNTER_VIEW,
            Permission.LAB_ORDER_VIEW,
            Permission.LAB_RESULT_WRITE
    )),

    PHARMACIST(Set.of(
            Permission.PATIENT_VIEW,
            Permission.PRESCRIPTION_VIEW,
            Permission.PHARMACY_VIEW,
            Permission.PHARMACY_DISPENSE,
            Permission.STOCK_VIEW,
            Permission.STOCK_MANAGE
    )),

    CASHIER(Set.of(
            Permission.PATIENT_VIEW,
            Permission.INVOICE_CREATE,
            Permission.INVOICE_VIEW,
            Permission.PAYMENT_RECORD,
            Permission.PAYMENT_VIEW,
            Permission.PAYMENT_WAIVE
    )),

    MANAGER(Set.of(
            Permission.USER_MANAGE,
            Permission.USER_VIEW
    ));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> permissions() {
        return permissions;
    }
}