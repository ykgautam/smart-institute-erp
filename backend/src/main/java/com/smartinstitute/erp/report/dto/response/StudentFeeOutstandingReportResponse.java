package com.smartinstitute.erp.report.dto.response;

import com.smartinstitute.erp.common.enums.fee.FeeStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Response DTO representing one student outstanding-fee record.
 *
 * <p>
 * This DTO exposes the financial information required by the
 * administrator's outstanding-fee report without exposing
 * database entities directly.
 * </p>
 */
@Getter
@AllArgsConstructor
public class StudentFeeOutstandingReportResponse {

    /**
     * Student identifier.
     */
    private Long studentId;

    /**
     * Student full name.
     */
    private String studentName;

    /**
     * Course in which the student is enrolled.
     */
    private String courseName;

    /**
     * Batch to which the student belongs.
     */
    private String batchName;

    /**
     * Original fee amount.
     */
    private BigDecimal totalFee;

    /**
     * Discount applied to the original fee.
     */
    private BigDecimal discount;

    /**
     * Final fee after discount.
     */
    private BigDecimal finalFee;

    /**
     * Amount already paid by the student.
     */
    private BigDecimal paidAmount;

    /**
     * Remaining amount payable by the student.
     */
    private BigDecimal pendingAmount;

    /**
     * Current fee status.
     */
    private FeeStatus feeStatus;

    /**
     * Due date of the fee.
     */
    private LocalDate feeDueDate;
}