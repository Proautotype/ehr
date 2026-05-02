package com.custard.ehr.payment.application.service;

import com.custard.ehr.encounter.EncounterPaymentUpdater;
import com.custard.ehr.encounter.application.ports.EncounterRepository;
import com.custard.ehr.payment.application.dto.PaymentResponse;
import com.custard.ehr.payment.application.dto.RecordPaymentRequest;
import com.custard.ehr.payment.application.ports.InvoiceRepository;
import com.custard.ehr.payment.application.ports.PaymentRepository;
import com.custard.ehr.payment.domain.Invoice;
import com.custard.ehr.payment.domain.InvoiceStatus;
import com.custard.ehr.payment.domain.Payment;
import com.custard.ehr.shared.events.InvoiceWaivedEvent;
import com.custard.ehr.shared.events.PaymentRecordedEvent;
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
public class PaymentService {

    private final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final EncounterPaymentUpdater encounterPaymentUpdater;
    private final ApplicationEventPublisher eventPublisher;

    public PaymentService(
            InvoiceRepository invoiceRepository,
            PaymentRepository paymentRepository,
            EncounterPaymentUpdater encounterPaymentUpdater,
            ApplicationEventPublisher eventPublisher
    ) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.encounterPaymentUpdater = encounterPaymentUpdater;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public PaymentResponse recordPayment(UUID invoiceId, RecordPaymentRequest request) {
        log.info("Recording payment for invoice {}. Amount={}", invoiceId, request.amount());

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> {
                    log.warn("Payment recording failed. Invoice {} not found", invoiceId);
                    return new NotFoundException("Invoice not found");
                });

        UUID receivedBy = SecurityUtils.requireCurrentUserId();

        invoice.applyPayment(request.amount());

        Payment payment = new Payment(
                invoice.getId(),
                invoice.getEncounterId(),
                invoice.getPatientId(),
                request.amount(),
                request.method(),
                request.reference(),
                receivedBy
        );

        Invoice savedInvoice = invoiceRepository.save(invoice);
        Payment savedPayment = paymentRepository.save(payment);

        eventPublisher.publishEvent(
                new PaymentRecordedEvent(
                        savedPayment.getId(),
                        savedPayment.getInvoiceId(),
                        savedPayment.getEncounterId(),
                        savedPayment.getPatientId(),
                        savedPayment.getAmount(),
                        savedPayment.getMethod().name(),
                        receivedBy,
                        Instant.now()
                )
        );

        if (savedInvoice.getStatus() == InvoiceStatus.PAID) {
            log.info(
                    "Invoice {} fully paid. Updating encounter {} payment status",
                    savedInvoice.getId(),
                    savedInvoice.getEncounterId()
            );

            encounterPaymentUpdater.markPaymentPaid(savedInvoice.getEncounterId());
        }

        log.info(
                "Payment {} recorded successfully for invoice {}. Invoice status={}",
                savedPayment.getId(),
                invoiceId,
                savedInvoice.getStatus()
        );

        return PaymentResponse.from(savedPayment);
    }

    @Transactional
    public void waiveInvoice(UUID invoiceId) {
        log.info("Waiving invoice {}", invoiceId);

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> {
                    log.warn("Invoice waiver failed. Invoice {} not found", invoiceId);
                    return new NotFoundException("Invoice not found");
                });

        if (invoice.getStatus() == InvoiceStatus.PARTIALLY_PAID) {
            throw new BusinessException("Partially paid invoice cannot be waived");
        }

        UUID waivedBy = SecurityUtils.requireCurrentUserId();

        invoice.waive();
        Invoice saved = invoiceRepository.save(invoice);

        encounterPaymentUpdater.waivePayment(saved.getEncounterId());

        eventPublisher.publishEvent(
                new InvoiceWaivedEvent(
                        saved.getId(),
                        saved.getEncounterId(),
                        saved.getPatientId(),
                        waivedBy,
                        Instant.now()
                )
        );

        log.info("Invoice {} waived successfully by {}", invoiceId, waivedBy);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByInvoice(UUID invoiceId) {
        log.debug("Fetching payments for invoice {}", invoiceId);

        return paymentRepository.findByInvoiceIdOrderByPaidAtDesc(invoiceId)
                .stream()
                .map(PaymentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByPatient(UUID patientId) {
        log.debug("Fetching payments for patient {}", patientId);

        return paymentRepository.findByPatientIdOrderByPaidAtDesc(patientId)
                .stream()
                .map(PaymentResponse::from)
                .toList();
    }
}