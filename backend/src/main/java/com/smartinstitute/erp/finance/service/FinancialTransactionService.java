package com.smartinstitute.erp.finance.service;

import com.smartinstitute.erp.finance.dto.request.FinancialTransactionRequest;
import com.smartinstitute.erp.finance.dto.response.FinancialTransactionResponse;
import com.smartinstitute.erp.finance.enums.FinancialTransactionType;

import java.util.List;

public interface FinancialTransactionService {

    FinancialTransactionResponse createTransaction(
            FinancialTransactionRequest request
    );

    FinancialTransactionResponse getTransaction(Long id);

    List<FinancialTransactionResponse> getTransactions(
            FinancialTransactionType type
    );

    FinancialTransactionResponse voidTransaction(Long id, String reason);
}