package com.smartinstitute.erp.finance.dto.response;

import com.smartinstitute.erp.finance.enums.FinancialTransactionType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FinancialCategoryResponse {

    private Long id;
    private String name;
    private FinancialTransactionType transactionType;
    private String description;
    private Boolean active;
}