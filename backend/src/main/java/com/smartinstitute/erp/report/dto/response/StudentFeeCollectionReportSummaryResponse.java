package com.smartinstitute.erp.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Aggregate summary for the Student Fee Collection Report.
 *
 * <p>
 * Summary values are calculated from the complete filtered dataset,
 * not only from the records present on the current pagination page.
 * </p>
 */
@Getter
@AllArgsConstructor
public class StudentFeeCollectionReportSummaryResponse {

    private Long totalStudents;

    private Long studentsWithPayments;

    private BigDecimal totalFee;

    private BigDecimal totalDiscount;

    private BigDecimal totalFinalFee;

    private BigDecimal totalPaidAmount;

    private BigDecimal totalPendingAmount;
}