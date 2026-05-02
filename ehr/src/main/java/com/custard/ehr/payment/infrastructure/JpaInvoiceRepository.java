package com.custard.ehr.payment.infrastructure;

import com.custard.ehr.payment.application.ports.InvoiceRepository;
import com.custard.ehr.payment.domain.Invoice;
import com.custard.ehr.payment.domain.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaInvoiceRepository
        extends JpaRepository<Invoice, UUID>, InvoiceRepository {

    Optional<Invoice> findByEncounterId(UUID encounterId);

    List<Invoice> findByPatientIdOrderByCreatedAtDesc(UUID patientId);

    List<Invoice> findByStatusOrderByCreatedAtAsc(InvoiceStatus status);

    boolean existsByEncounterId(UUID encounterId);
}