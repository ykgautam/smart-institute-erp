package com.smartinstitute.erp.finance.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FinancialMonthlyTrendResponse {

    // First calendar day of the month; the frontend can format it as "Oct 2026".
    private final LocalDate monthStart;

    private final BigDecimal totalIncome;
    private final BigDecimal totalExpense;
    private final BigDecimal netBalance;
}