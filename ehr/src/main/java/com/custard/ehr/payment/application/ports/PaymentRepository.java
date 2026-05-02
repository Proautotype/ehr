package com.custard.ehr.payment.application.ports;

import com.custard.ehr.payment.domain.Payment;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository {

    Payment save(Payment payment);

    List<Payment> findByInvoiceIdOrderByPaidAtDesc(UUID invoiceId);

    List<Payment> findByPatientIdOrderByPaidAtDesc(UUID patientId);
}