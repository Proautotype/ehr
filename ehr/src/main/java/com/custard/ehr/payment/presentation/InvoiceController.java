package com.custard.ehr.payment.presentation;

import com.custard.ehr.payment.application.dto.CreateInvoiceRequest;
import com.custard.ehr.payment.application.dto.InvoiceResponse;
import com.custard.ehr.payment.application.service.InvoiceService;
import com.custard.ehr.payment.domain.InvoiceStatus;
import com.custard.ehr.shared.infrastruture.web.AppApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoices")
@Slf4j
@Tag(name = "Invoices", description = "Billing and invoice management APIs")
@SecurityRequirement(name = "bearerAuth")
public class InvoiceController {

    private final Logger log = LoggerFactory.getLogger(InvoiceController.class);

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    @Operation(
            summary = "Create invoice",
            description = "Creates an invoice for a given encounter"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Invoice created successfully",
                    content = @Content(schema = @Schema(implementation = InvoiceResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Encounter not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<InvoiceResponse> create(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Invoice creation payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateInvoiceRequest.class))
            )
            @RequestBody CreateInvoiceRequest request
    ) {
        log.info("Received request to create invoice for encounter {}", request.encounterId());

        return AppApiResponse.success(
                "Invoice created successfully",
                invoiceService.create(request)
        );
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get invoice by ID",
            description = "Fetch an invoice by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Invoice retrieved successfully",
                    content = @Content(schema = @Schema(implementation = InvoiceResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Invoice not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<InvoiceResponse> getById(@PathVariable UUID id) {
        return AppApiResponse.success(invoiceService.getById(id));
    }

    @GetMapping("/encounter/{encounterId}")
    @Operation(
            summary = "Get invoice by encounter",
            description = "Fetch the invoice associated with a specific encounter"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Invoice retrieved successfully",
                    content = @Content(schema = @Schema(implementation = InvoiceResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Invoice or encounter not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<InvoiceResponse> getByEncounter(@PathVariable UUID encounterId) {
        return AppApiResponse.success(invoiceService.getByEncounter(encounterId));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(
            summary = "Get invoices by patient",
            description = "Fetch all invoices associated with a patient"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Invoices retrieved successfully",
                    content = @Content(schema = @Schema(implementation = InvoiceResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<List<InvoiceResponse>> getByPatient(@PathVariable UUID patientId) {
        return AppApiResponse.success(invoiceService.getByPatient(patientId));
    }

    @GetMapping("/filter/{status}")
    @Operation(
            summary = "Get unpaid invoices",
            description = "Fetch all invoices that are not yet paid"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Unpaid invoices retrieved successfully",
                    content = @Content(schema = @Schema(implementation = InvoiceResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<List<InvoiceResponse>> getUnpaidInvoices(@PathVariable("status") String status) {
        var invoiceStatus = InvoiceStatus.valueOf(status);
        return AppApiResponse.success(invoiceService.getUnpaidInvoices(invoiceStatus));
    }
}