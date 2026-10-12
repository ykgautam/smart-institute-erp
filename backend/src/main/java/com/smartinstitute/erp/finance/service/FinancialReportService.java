package com.smartinstitute.erp.finance.service;

import java.time.LocalDate;
import java.util.List;

import com.smartinstitute.erp.finance.dto.response.FinancialCategoryBreakdownResponse;
import com.smartinstitute.erp.finance.dto.response.FinancialMonthlyTrendResponse;
import com.smartinstitute.erp.finance.dto.response.FinancialSummaryResponse;
import com.smartinstitute.erp.finance.enums.FinancialTransactionType;

public interface FinancialReportService {

    FinancialSummaryResponse getSummary(LocalDate fromDate, LocalDate toDate);

//    FinancialMonthlyTrendResponse[] unusedPlaceholder();

    List<FinancialMonthlyTrendResponse> getMonthlyTrends(
            LocalDate fromDate,
            LocalDate toDate);

    List<FinancialCategoryBreakdownResponse> getCategoryBreakdown(
            FinancialTransactionType type,
            LocalDate fromDate,
            LocalDate toDate);
}