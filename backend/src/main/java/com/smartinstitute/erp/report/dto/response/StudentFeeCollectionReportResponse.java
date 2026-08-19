package com.smartinstitute.erp.report.dto.response;

import com.smartinstitute.erp.common.enums.fee.FeeStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Individual student fee collection record.
 */
@Getter
@AllArgsConstructor
public class StudentFeeCollectionReportResponse {

    private Long studentId;

    private String studentName;

    private String courseName;

    private String batchName;

    private BigDecimal totalFee;

    private BigDecimal discount;

    private BigDecimal finalFee;

    private BigDecimal paidAmount;

    private BigDecimal pendingAmount;

    private FeeStatus feeStatus;

    /**
     * Fee due date for the student.
     */
    private LocalDate feeDueDate;
}