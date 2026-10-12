package com.smartinstitute.erp.finance.dto.request;

import com.smartinstitute.erp.finance.enums.FinancialTransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinancialCategoryRequest {

    @NotBlank(message = "Category name is required.")
    @Size(max = 100, message = "Category name cannot exceed 100 characters.")
    private String name;

    @NotNull(message = "Transaction type is required.")
    private FinancialTransactionType transactionType;

    @Size(max = 500, message = "Description cannot exceed 500 characters.")
    private String description;
}