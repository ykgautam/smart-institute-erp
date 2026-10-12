package com.smartinstitute.erp.finance.mapper;

import com.smartinstitute.erp.finance.dto.response.FinancialCategoryResponse;
import com.smartinstitute.erp.finance.dto.response.FinancialTransactionResponse;
import com.smartinstitute.erp.finance.entity.FinancialCategory;
import com.smartinstitute.erp.finance.entity.FinancialTransaction;
import org.springframework.stereotype.Component;

@Component
public class FinancialMapper {

    public FinancialCategoryResponse toCategoryResponse(
            FinancialCategory category
    ) {
        return FinancialCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .transactionType(category.getTransactionType())
                .description(category.getDescription())
                .active(category.getActive())
                .build();
    }

    public FinancialTransactionResponse toTransactionResponse(
            FinancialTransaction transaction
    ) {
        return FinancialTransactionResponse.builder()
                .id(transaction.getId())
                .categoryId(transaction.getCategory().getId())
                .categoryName(transaction.getCategory().getName())
                .transactionType(transaction.getTransactionType())
                .transactionDate(transaction.getTransactionDate())
                .amount(transaction.getAmount())
                .paymentMode(transaction.getPaymentMode())
                .referenceNumber(transaction.getReferenceNumber())
                .partyName(transaction.getPartyName())
                .description(transaction.getDescription())
                .sourceType(transaction.getSourceType())
                .sourceId(transaction.getSourceId())
                .status(transaction.getStatus())
                .voidReason(transaction.getVoidReason())
                .voidedAt(transaction.getVoidedAt())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
}