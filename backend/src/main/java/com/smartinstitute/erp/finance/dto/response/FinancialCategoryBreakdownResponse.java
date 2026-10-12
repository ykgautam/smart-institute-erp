package com.smartinstitute.erp.finance.dto.response;

import java.math.BigDecimal;

import com.smartinstitute.erp.finance.enums.FinancialTransactionType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FinancialCategoryBreakdownResponse {

    private final Long categoryId;
    private final String categoryName;
    private final FinancialTransactionType transactionType;
    private final BigDecimal totalAmount;
}