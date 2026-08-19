package com.smartinstitute.erp.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Summary section for the Student Fee Outstanding Report.
 *
 * <p>
 * This object provides institute-level financial totals for the
 * filtered student population. It is independent of pagination.
 * </p>
 *
 * <p>
 * The summary contains both:
 * <ul>
 *     <li>Student counts</li>
 *     <li>Outstanding student count</li>
 *     <li>Total fee amounts</li>
 *     <li>Total paid amount</li>
 *     <li>Total pending amount</li>
 * </ul>
 * </p>
 */
@Getter
@AllArgsConstructor
public class StudentFeeOutstandingReportSummaryResponse {

    /**
     * Total active students matching the report filters.
     */
    private Long totalStudents;

    /**
     * Number of students having an outstanding/pending fee amount.
     */
    private Long studentsWithOutstandingFees;

    /**
     * Total original fee amount across the filtered students.
     */
    private BigDecimal totalFee;

    /**
     * Total discount amount across the filtered students.
     */
    private BigDecimal totalDiscount;

    /**
     * Total final fee after discount.
     */
    private BigDecimal totalFinalFee;

    /**
     * Total amount already paid by the filtered students.
     */
    private BigDecimal totalPaidAmount;

    /**
     * Total amount still pending.
     */
    private BigDecimal totalPendingAmount;
}