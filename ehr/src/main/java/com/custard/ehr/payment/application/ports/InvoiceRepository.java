package com.custard.ehr.payment.application.ports;

import com.custard.ehr.payment.domain.Invoice;
import com.custard.ehr.payment.domain.InvoiceStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository {

    Invoice save(Invoice invoice);

    Optional<Invoice> findById(UUID id);

    Optional<Invoice> findByEncounterId(UUID encounterId);

    List<Invoice> findByPatientIdOrderByCreatedAtDesc(UUID patientId);

    List<Invoice> findByStatusOrderByCreatedAtAsc(InvoiceStatus status);

    boolean existsByEncounterId(UUID encounterId);
}