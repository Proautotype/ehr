package com.custard.ehr.payment.infrastructure.jpaRepository;

import com.custard.ehr.payment.application.ports.PaymentRepository;
import com.custard.ehr.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaPaymentRepository
        extends JpaRepository<Payment, UUID>, PaymentRepository {

    List<Payment> findByInvoiceIdOrderByPaidAtDesc(UUID invoiceId);

    List<Payment> findByPatientIdOrderByPaidAtDesc(UUID patientId);
}