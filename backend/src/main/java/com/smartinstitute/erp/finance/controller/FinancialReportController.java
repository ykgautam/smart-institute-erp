package com.smartinstitute.erp.finance.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.finance.dto.response.FinancialSummaryResponse;
import com.smartinstitute.erp.finance.service.FinancialReportService;
import java.util.List;
import com.smartinstitute.erp.finance.dto.response.FinancialCategoryBreakdownResponse;
import com.smartinstitute.erp.finance.dto.response.FinancialMonthlyTrendResponse;
import com.smartinstitute.erp.finance.enums.FinancialTransactionType;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/finance/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
public class FinancialReportController {

    private final FinancialReportService financialReportService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<FinancialSummaryResponse>> getSummary(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate) {

        FinancialSummaryResponse summary =
                financialReportService.getSummary(fromDate, toDate);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        summary,
                        "Financial summary retrieved successfully."));
    }

    @GetMapping("/monthly-trends")
    public ResponseEntity<ApiResponse<List<FinancialMonthlyTrendResponse>>> getMonthlyTrends(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate) {

        List<FinancialMonthlyTrendResponse> trends =
                financialReportService.getMonthlyTrends(fromDate, toDate);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        trends, "Monthly finance trends retrieved successfully."));
    }

    @GetMapping("/category-breakdown")
    public ResponseEntity<ApiResponse<List<FinancialCategoryBreakdownResponse>>> getCategoryBreakdown(
            @RequestParam FinancialTransactionType type,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate) {

        List<FinancialCategoryBreakdownResponse> breakdown =
                financialReportService.getCategoryBreakdown(
                        type, fromDate, toDate);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        breakdown, "Category breakdown retrieved successfully."));
    }

}