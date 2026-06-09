package com.custard.ehr.payment.infrastructure.eventListener;

import com.custard.ehr.encounter.EncounterModifier;
import com.custard.ehr.payment.application.dto.InvoiceItemType;
import com.custard.ehr.payment.application.ports.InvoiceRepository;
import com.custard.ehr.payment.domain.Invoice;
import com.custard.ehr.payment.domain.InvoiceItem;
import com.custard.ehr.shared.events.PatientRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class InvoiceListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final InvoiceRepository invoiceRepository;

    private final EncounterModifier encounterModifier;

    public InvoiceListener(InvoiceRepository invoiceRepository, EncounterModifier encounterModifier) {
        this.invoiceRepository = invoiceRepository;
        this.encounterModifier = encounterModifier;
    }

    @ApplicationModuleListener
    public void onPatientRegistered(PatientRegisteredEvent event) {
        logger.info("Patient registered event received at invoice module");

        // create default encounter
        UUID encounterId = encounterModifier.create(
                event.patientId(),
                "",
                event.registeredBy()
        );

        Invoice invoice = new Invoice(
                encounterId,
                event.patientId(),
                event.registeredBy()
        );
        invoice.setPatientNumber(event.patientNumber());
        Invoice savedInvoice = invoiceRepository.save(invoice);

        InvoiceItem invoiceItem = new InvoiceItem(
                savedInvoice, InvoiceItemType.REGISTER_PATIENT, BigDecimal.valueOf(50)
        );

        invoiceItem.setPatientId(event.patientId().toString());
        invoiceItem.setItemReferenceId(event.patientNumber());

        savedInvoice.addItem(invoiceItem);
    }
}
