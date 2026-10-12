package com.smartinstitute.erp.finance.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.smartinstitute.erp.security.util.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.smartinstitute.erp.common.exception.BadRequestException;
import com.smartinstitute.erp.finance.dto.response.FinancialSummaryResponse;
import com.smartinstitute.erp.finance.enums.FinancialTransactionStatus;
import com.smartinstitute.erp.finance.enums.FinancialTransactionType;
import com.smartinstitute.erp.finance.repository.FinancialTransactionRepository;
import com.smartinstitute.erp.finance.service.FinancialReportService;
import com.smartinstitute.erp.institute.entity.Institute;
import java.util.List;
import com.smartinstitute.erp.finance.dto.response.FinancialCategoryBreakdownResponse;
import com.smartinstitute.erp.finance.dto.response.FinancialMonthlyTrendResponse;

@Service
public class FinancialReportServiceImpl implements FinancialReportService {

    private final FinancialTransactionRepository transactionRepository;
    private final SecurityUtil securityUtil;

    public FinancialReportServiceImpl(
            FinancialTransactionRepository transactionRepository,
            SecurityUtil securityUtil) {
        this.transactionRepository = transactionRepository;
        this.securityUtil = securityUtil;
    }

    @Override
    @Transactional(readOnly = true)
    public FinancialSummaryResponse getSummary(
            LocalDate fromDate,
            LocalDate toDate) {

        if (fromDate == null || toDate == null) {
            throw new BadRequestException(
                    "Both fromDate and toDate are required.");
        }

        if (fromDate.isAfter(toDate)) {
            throw new BadRequestException(
                    "fromDate cannot be after toDate.");
        }

        // The authenticated user's institute is the tenant boundary.
        // Never accept an arbitrary institute ID from the request.
        Institute institute = securityUtil.getCurrentInstitute();

        BigDecimal totalIncome = getTotal(
                institute,
                FinancialTransactionType.INCOME,
                fromDate,
                toDate);

        BigDecimal totalExpense = getTotal(
                institute,
                FinancialTransactionType.EXPENSE,
                fromDate,
                toDate);

        BigDecimal netBalance = totalIncome.subtract(totalExpense);

        return new FinancialSummaryResponse(
                fromDate,
                toDate,
                totalIncome,
                totalExpense,
                netBalance);
    }

    private BigDecimal getTotal(
            Institute institute,
            FinancialTransactionType transactionType,
            LocalDate fromDate,
            LocalDate toDate) {

        BigDecimal total = transactionRepository
                .sumByInstituteAndTypeAndStatusAndDateRange(
                        institute,
                        transactionType,
                        FinancialTransactionStatus.POSTED,
                        fromDate,
                        toDate);

        // Keep the API contract predictable even when no transactions exist.
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialMonthlyTrendResponse> getMonthlyTrends(
            LocalDate fromDate,
            LocalDate toDate) {

        validateDateRange(fromDate, toDate);

        // The native query needs the authenticated tenant's ID, not a request parameter.
        Long instituteId = securityUtil.getCurrentInstituteId();

        return transactionRepository
                .findMonthlyFinanceAggregates(instituteId, fromDate, toDate)
                .stream()
                .map(row -> {
                    BigDecimal income = row.getTotalIncome() != null
                            ? row.getTotalIncome() : BigDecimal.ZERO;
                    BigDecimal expense = row.getTotalExpense() != null
                            ? row.getTotalExpense() : BigDecimal.ZERO;

                    return new FinancialMonthlyTrendResponse(
                            row.getMonthStart(),
                            income,
                            expense,
                            income.subtract(expense));
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialCategoryBreakdownResponse> getCategoryBreakdown(
            FinancialTransactionType type,
            LocalDate fromDate,
            LocalDate toDate) {

        validateDateRange(fromDate, toDate);

        if (type == null) {
            throw new BadRequestException(
                    "Transaction type must be INCOME or EXPENSE.");
        }

        Institute institute = securityUtil.getCurrentInstitute();

        return transactionRepository.findCategoryBreakdown(
                institute,
                type,
                FinancialTransactionStatus.POSTED,
                fromDate,
                toDate);
    }

    /**
     * Shared validation keeps all finance reports consistent.
     */
    private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null || toDate == null) {
            throw new BadRequestException(
                    "Both fromDate and toDate are required.");
        }

        if (fromDate.isAfter(toDate)) {
            throw new BadRequestException(
                    "fromDate cannot be after toDate.");
        }
    }
}