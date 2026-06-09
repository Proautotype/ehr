package com.custard.ehr.payment.infrastructure.jpaRepository;

import com.custard.ehr.payment.application.ports.InvoiceRepository;
import com.custard.ehr.payment.domain.Invoice;
import com.custard.ehr.payment.domain.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaInvoiceRepository
        extends JpaRepository<Invoice, UUID>, InvoiceRepository {

    Optional<Invoice> findByEncounterId(UUID encounterId);

    List<Invoice> findByPatientIdOrderByCreatedAtDesc(UUID patientId);

    List<Invoice> findByStatusOrderByCreatedAtAsc(InvoiceStatus status);

    @Query(value = "SELECT " +
            "  i.id AS id, " +
            "  i.encounter_id AS encounterId, " +
            "  i.patient_id AS patientId, " +
            "  CONCAT(p.first_name, ' ', p.last_name) AS patientName, " +
            "  i.created_by AS createdBy, " +
            "  i.created_at AS createdAt, " +
            "  i.total_amount AS totalAmount, " +
            "  i.amount_paid AS amountPaid, " +
            "  i.status AS status " +
            "FROM invoices i " +
            "INNER JOIN patients p ON i.patient_id = p.id " +
            "WHERE i.status = :#{#status.name()} " + // Safely passes Enum string name to SQL
            "ORDER BY i.created_at ASC",
            nativeQuery = true)
    List<Invoice> findByStatus(InvoiceStatus status);

    boolean existsByEncounterId(UUID encounterId);
}