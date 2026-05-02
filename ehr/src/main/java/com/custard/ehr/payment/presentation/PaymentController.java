package com.custard.ehr.payment.presentation;

import com.custard.ehr.payment.application.dto.PaymentResponse;
import com.custard.ehr.payment.application.dto.RecordPaymentRequest;
import com.custard.ehr.payment.application.service.PaymentService;
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
@RequestMapping("/api/v1/payments")
@Slf4j
@Tag(name = "Payments", description = "Payment processing and transaction management APIs")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/invoice/{invoiceId}")
    @Operation(
            summary = "Record payment",
            description = "Records a payment against a specific invoice"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment recorded successfully",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Invoice not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<PaymentResponse> recordPayment(
            @PathVariable UUID invoiceId,
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Payment payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RecordPaymentRequest.class))
            )
            @RequestBody RecordPaymentRequest request
    ) {
        log.info("Received request to record payment for invoice {}", invoiceId);

        return AppApiResponse.success(
                "Payment recorded successfully",
                paymentService.recordPayment(invoiceId, request)
        );
    }

    @PatchMapping("/invoice/{invoiceId}/waive")
    @Operation(
            summary = "Waive invoice",
            description = "Waives an invoice, marking it as not requiring payment"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Invoice waived successfully"
            ),
            @ApiResponse(responseCode = "404", description = "Invoice not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<Void> waiveInvoice(@PathVariable UUID invoiceId) {
        log.info("Received request to waive invoice {}", invoiceId);

        paymentService.waiveInvoice(invoiceId);

        return AppApiResponse.success("Invoice waived successfully", null);
    }

    @GetMapping("/invoice/{invoiceId}")
    @Operation(
            summary = "Get payments by invoice",
            description = "Fetch all payments associated with a specific invoice"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payments retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Invoice not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<List<PaymentResponse>> getPaymentsByInvoice(
            @PathVariable UUID invoiceId
    ) {
        return AppApiResponse.success(paymentService.getPaymentsByInvoice(invoiceId));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(
            summary = "Get payments by patient",
            description = "Fetch all payments made by a specific patient"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payments retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public AppApiResponse<List<PaymentResponse>> getPaymentsByPatient(
            @PathVariable UUID patientId
    ) {
        return AppApiResponse.success(paymentService.getPaymentsByPatient(patientId));
    }
}