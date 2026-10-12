package com.smartinstitute.erp.finance.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.finance.dto.request.FinancialTransactionRequest;
import com.smartinstitute.erp.finance.dto.response.FinancialTransactionResponse;
import com.smartinstitute.erp.finance.enums.FinancialTransactionType;
import com.smartinstitute.erp.finance.service.FinancialTransactionService;
import com.smartinstitute.erp.finance.dto.request.VoidFinancialTransactionRequest;
import org.springframework.web.bind.annotation.PatchMapping;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/finance/transactions")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
public class FinancialTransactionController {

    private final FinancialTransactionService financialTransactionService;

    /**
     * Creates a manual income or expense transaction.
     *
     * Source type, source ID and status are controlled by the backend,
     * preventing clients from impersonating automatic fee-payment entries.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<FinancialTransactionResponse>>
    createTransaction(
            @Valid @RequestBody FinancialTransactionRequest request) {

        FinancialTransactionResponse response =
                financialTransactionService.createTransaction(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseUtil.success(
                        response,
                        "Financial transaction created successfully."
                )
        );
    }

    /**
     * Lists transactions for the authenticated institute.
     * The optional type filter accepts the existing enum values: INCOME or EXPENSE.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<FinancialTransactionResponse>>>
    getTransactions(
            @RequestParam(required = false) FinancialTransactionType type) {

        List<FinancialTransactionResponse> response =
                financialTransactionService.getTransactions(type);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        response,
                        "Financial transactions fetched successfully."
                )
        );
    }

    /**
     * Retrieves a transaction only when it belongs to the current institute.
     * Tenant isolation is enforced by the service/repository, not this URL.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FinancialTransactionResponse>>
    getTransaction(@PathVariable Long id) {

        FinancialTransactionResponse response =
                financialTransactionService.getTransaction(id);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        response,
                        "Financial transaction fetched successfully."
                )
        );
    }

    /**
     * Voids a financial transaction without deleting its historical record.
     * Controller authorization and service-level tenant checks work together:
     * neither a valid ID nor a supplied reason grants access to another institute.
     */
    @PatchMapping("/{id}/void")
    public ResponseEntity<ApiResponse<FinancialTransactionResponse>> voidTransaction(
            @PathVariable Long id,
            @Valid @RequestBody VoidFinancialTransactionRequest request) {

        FinancialTransactionResponse response =
                financialTransactionService.voidTransaction(
                        id,
                        request.getReason()
                );

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        response,
                        "Financial transaction voided successfully."
                )
        );
    }
}