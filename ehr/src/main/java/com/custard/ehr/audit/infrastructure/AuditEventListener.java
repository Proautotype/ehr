package com.custard.ehr.audit.infrastructure;

import com.custard.ehr.audit.application.AuditService;
import com.custard.ehr.shared.events.*;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class AuditEventListener {

    private final AuditService auditService;

    public AuditEventListener(AuditService auditService) {
        this.auditService = auditService;
    }

    @ApplicationModuleListener
    public void onUserRegistered(UserRegisteredEvent event) {
        auditService.record(
                "USER_REGISTERED",
                "identity",
                "User",
                event.userId(),
                event.userId(),
                event.username(),
                event.registeredAt(),
                "User registered with email: " + event.email()
        );
    }

    @ApplicationModuleListener
    public void onUserLoggedIn(UserLoggedInEvent event) {
        auditService.record(
                "USER_LOGGED_IN",
                "identity",
                "User",
                event.userId(),
                event.userId(),
                event.username(),
                event.loginAt(),
                "User logged in successfully"
        );
    }

    @ApplicationModuleListener
    public void onPatientRegistered(PatientRegisteredEvent event) {
        auditService.record(
                "PATIENT_REGISTERED",
                "patient",
                "Patient",
                event.patientId(),
                event.registeredBy(),
                null,
                event.registeredAt(),
                "Patient registered with number: " + event.patientNumber()
        );
    }

    @ApplicationModuleListener
    public void onEncounterCompleted(EncounterCompletedEvent event) {
        auditService.record(
                "ENCOUNTER_COMPLETED",
                "encounter",
                "Encounter",
                event.encounterId(),
                event.completedBy(),
                null,
                event.completedAt(),
                "Encounter completed for patient: " + event.patientId()
        );
    }

    @ApplicationModuleListener
    public void onVitalsRecorded(VitalsRecordedEvent event) {
        auditService.record(
                "VITALS_RECORDED",
                "vitals",
                "Vitals",
                event.vitalsId(),
                event.recordedBy(),
                null,
                event.recordedAt(),
                "Vitals recorded for patient: " + event.patientId() +
                        " under encounter: " + event.encounterId()
        );
    }

    @ApplicationModuleListener
    public void onConsultationRecorded(ConsultationRecordedEvent event) {
        auditService.record(
                "CONSULTATION_RECORDED",
                "consultation",
                "ConsultationNote",
                event.consultationId(),
                event.doctorId(),
                null,
                event.recordedAt(),
                "Consultation recorded for patient: " + event.patientId() +
                        " under encounter: " + event.encounterId()
        );
    }

    @ApplicationModuleListener
    public void onPrescriptionCreated(PrescriptionCreatedEvent event) {
        auditService.record(
                "PRESCRIPTION_CREATED",
                "prescription",
                "Prescription",
                event.prescriptionId(),
                event.prescribedBy(),
                null,
                event.prescribedAt(),
                "Prescription created for patient: " + event.patientId() +
                        " under encounter: " + event.encounterId()
        );
    }

    @ApplicationModuleListener
    public void onPrescriptionDispensed(PrescriptionDispensedEvent event) {
        auditService.record(
                event.partial() ? "PRESCRIPTION_PARTIALLY_DISPENSED" : "PRESCRIPTION_DISPENSED",
                "pharmacy",
                "DispenseRecord",
                event.dispenseRecordId(),
                event.dispensedBy(),
                null,
                event.dispensedAt(),
                "Prescription " + event.prescriptionId() +
                        " processed for patient: " + event.patientId() +
                        ". Partial: " + event.partial()
        );
    }

    @ApplicationModuleListener
    public void onPrescriptionItemNotDispensed(PrescriptionItemNotDispensedEvent event) {
        auditService.record(
                "PRESCRIPTION_ITEM_NOT_FULLY_DISPENSED",
                "pharmacy",
                "PrescriptionItem",
                event.prescriptionItemId(),
                event.recordedBy(),
                null,
                event.recordedAt(),
                "Drug " + event.drugName() +
                        " was not fully dispensed. Reason: " + event.reason()
        );
    }

    @ApplicationModuleListener
    public void onInvoiceCreated(InvoiceCreatedEvent event) {
        auditService.record(
                "INVOICE_CREATED",
                "payment",
                "Invoice",
                event.invoiceId(),
                event.createdBy(),
                null,
                event.createdAt(),
                "Invoice created for patient: " + event.patientId() +
                        " with total amount: " + event.totalAmount()
        );
    }

    @ApplicationModuleListener
    public void onPaymentRecorded(PaymentRecordedEvent event) {
        auditService.record(
                "PAYMENT_RECORDED",
                "payment",
                "Payment",
                event.paymentId(),
                event.receivedBy(),
                null,
                event.paidAt(),
                "Payment of " + event.amount() +
                        " recorded using " + event.method()
        );
    }

    @ApplicationModuleListener
    public void onInvoiceWaived(InvoiceWaivedEvent event) {
        auditService.record(
                "INVOICE_WAIVED",
                "payment",
                "Invoice",
                event.invoiceId(),
                event.waivedBy(),
                null,
                event.waivedAt(),
                "Invoice waived for patient: " + event.patientId()
        );
    }

    @ApplicationModuleListener
    public void onLabOrderCreated(LabOrderCreatedEvent event) {
        auditService.record(
                "LAB_ORDER_CREATED",
                "laboratory",
                "LabOrder",
                event.labOrderId(),
                event.orderedBy(),
                null,
                event.orderedAt(),
                "Lab order created for patient: " + event.patientId() +
                        " under encounter: " + event.encounterId()
        );
    }

    @ApplicationModuleListener
    public void onLabResultRecorded(LabResultRecordedEvent event) {
        auditService.record(
                event.orderCompleted() ? "LAB_ORDER_COMPLETED" : "LAB_RESULT_RECORDED",
                "laboratory",
                "LabOrder",
                event.labOrderId(),
                event.recordedBy(),
                null,
                event.recordedAt(),
                "Lab result recorded for order item: " + event.labOrderItemId() +
                        ". Order completed: " + event.orderCompleted()
        );
    }

    @ApplicationModuleListener
    public void onUserRoleAssigned(UserRoleAssignedEvent event) {
        auditService.record(
                "USER_ROLE_ASSIGNED",
                "identity",
                "User",
                event.userId(),
                event.assignedBy(),
                null,
                event.assignedAt(),
                "Role " + event.role() + " assigned to user: " + event.username()
        );
    }

    @ApplicationModuleListener
    public void onUserActivated(UserActivatedEvent event) {
        auditService.record(
                "USER_ACTIVATED",
                "identity",
                "User",
                event.userId(),
                event.activatedBy(),
                null,
                event.activatedAt(),
                "User activated: " + event.username()
        );
    }

    @ApplicationModuleListener
    public void onUserDeactivated(UserDeactivatedEvent event) {
        auditService.record(
                "USER_DEACTIVATED",
                "identity",
                "User",
                event.userId(),
                event.deactivatedBy(),
                null,
                event.deactivatedAt(),
                "User deactivated: " + event.username()
        );
    }

    @ApplicationModuleListener
    public void onUserPasswordReset(UserPasswordResetEvent event) {
        auditService.record(
                "USER_PASSWORD_RESET",
                "identity",
                "User",
                event.userId(),
                event.resetBy(),
                null,
                event.resetAt(),
                "Password reset for user: " + event.username()
        );
    }

}