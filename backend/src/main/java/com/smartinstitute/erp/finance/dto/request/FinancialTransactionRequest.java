package com.smartinstitute.erp.finance.dto.request;

import com.smartinstitute.erp.common.enums.fee.PaymentMode;
import com.smartinstitute.erp.finance.enums.FinancialTransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class FinancialTransactionRequest {

    @NotNull(message = "Category is required.")
    @Positive(message = "Category ID must be positive.")
    private Long categoryId;

    @NotNull(message = "Transaction type is required.")
    private FinancialTransactionType transactionType;

    @NotNull(message = "Transaction date is required.")
    @PastOrPresent(message = "Transaction date cannot be in the future.")
    private LocalDate transactionDate;

    @NotNull(message = "Amount is required.")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero.")
    private BigDecimal amount;

    @NotNull(message = "Payment mode is required.")
    private PaymentMode paymentMode;

    @Size(max = 100, message = "Reference number cannot exceed 100 characters.")
    private String referenceNumber;

    @Size(max = 200, message = "Party name cannot exceed 200 characters.")
    private String partyName;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters.")
    private String description;
}