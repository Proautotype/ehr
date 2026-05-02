package com.custard.ehr.payment.application.service;

import com.custard.ehr.encounter.EncounterIdentifierVerifier;
import com.custard.ehr.encounter.EncounterLookupView;
import com.custard.ehr.payment.application.dto.CreateInvoiceRequest;
import com.custard.ehr.payment.application.dto.InvoiceResponse;
import com.custard.ehr.payment.application.ports.InvoiceRepository;
import com.custard.ehr.payment.domain.Invoice;
import com.custard.ehr.payment.domain.InvoiceStatus;
import com.custard.ehr.shared.events.InvoiceCreatedEvent;
import com.custard.ehr.shared.exception.BusinessException;
import com.custard.ehr.shared.exception.NotFoundException;
import com.custard.ehr.shared.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class InvoiceService {

    private final Logger log = LoggerFactory.getLogger(InvoiceService.class);

    private final InvoiceRepository invoiceRepository;
    private final EncounterIdentifierVerifier encounterIdentifierVerifier;
    private final ApplicationEventPublisher eventPublisher;

    public InvoiceService(
            InvoiceRepository invoiceRepository,
            EncounterIdentifierVerifier encounterIdentifierVerifier,
            ApplicationEventPublisher eventPublisher
    ) {
        this.invoiceRepository = invoiceRepository;
        this.encounterIdentifierVerifier = encounterIdentifierVerifier;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public InvoiceResponse create(CreateInvoiceRequest request) {
        log.info("Creating invoice for encounter {}", request.encounterId());

        if (invoiceRepository.existsByEncounterId(request.encounterId())) {
            log.warn("Invoice creation blocked. Encounter {} already has invoice", request.encounterId());
            throw new BusinessException("Invoice already exists for this encounter");
        }

        EncounterLookupView encounter = encounterIdentifierVerifier
                .findActiveEncounter(request.encounterId())
                .orElseThrow(() -> {
                    log.warn("Invoice creation blocked. Encounter {} is not active or does not exist", request.encounterId());
                    return new BusinessException("Encounter is not active or does not exist");
                });

        UUID createdBy = SecurityUtils.requireCurrentUserId();

        Invoice invoice = new Invoice(
                encounter.encounterId(),
                encounter.patientId(),
                createdBy
        );

        request.items().forEach(item ->
                invoice.addItem(item.description(), item.amount())
        );

        Invoice saved = invoiceRepository.save(invoice);

        eventPublisher.publishEvent(
                new InvoiceCreatedEvent(
                        saved.getId(),
                        saved.getEncounterId(),
                        saved.getPatientId(),
                        saved.getTotalAmount(),
                        createdBy,
                        Instant.now()
                )
        );

        log.info(
                "Invoice {} created for encounter {} with total amount {}",
                saved.getId(),
                saved.getEncounterId(),
                saved.getTotalAmount()
        );

        return InvoiceResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getById(UUID invoiceId) {
        log.debug("Fetching invoice {}", invoiceId);

        return invoiceRepository.findById(invoiceId)
                .map(InvoiceResponse::from)
                .orElseThrow(() -> new NotFoundException("Invoice not found"));
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getByEncounter(UUID encounterId) {
        log.debug("Fetching invoice for encounter {}", encounterId);

        return invoiceRepository.findByEncounterId(encounterId)
                .map(InvoiceResponse::from)
                .orElseThrow(() -> new NotFoundException("Invoice not found for encounter"));
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getByPatient(UUID patientId) {
        log.debug("Fetching invoices for patient {}", patientId);

        return invoiceRepository.findByPatientIdOrderByCreatedAtDesc(patientId)
                .stream()
                .map(InvoiceResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getUnpaidInvoices() {
        log.debug("Fetching unpaid invoices");

        return invoiceRepository.findByStatusOrderByCreatedAtAsc(InvoiceStatus.UNPAID)
                .stream()
                .map(InvoiceResponse::from)
                .toList();
    }
}