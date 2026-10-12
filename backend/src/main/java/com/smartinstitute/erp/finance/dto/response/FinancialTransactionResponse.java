package com.smartinstitute.erp.finance.dto.response;

import com.smartinstitute.erp.common.enums.fee.PaymentMode;
import com.smartinstitute.erp.finance.enums.FinancialSourceType;
import com.smartinstitute.erp.finance.enums.FinancialTransactionStatus;
import com.smartinstitute.erp.finance.enums.FinancialTransactionType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class FinancialTransactionResponse {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private FinancialTransactionType transactionType;
    private LocalDate transactionDate;
    private BigDecimal amount;
    private PaymentMode paymentMode;
    private String referenceNumber;
    private String partyName;
    private String description;
    private FinancialSourceType sourceType;
    private Long sourceId;
    private FinancialTransactionStatus status;
    private String voidReason;
    private LocalDateTime voidedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}