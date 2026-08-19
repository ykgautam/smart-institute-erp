package com.smartinstitute.erp.report.projection;

import com.smartinstitute.erp.common.enums.fee.FeeStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Projection used by the Student Fee Outstanding Report.
 *
 * <p>
 * Contains only the fields required by the reporting API instead of
 * loading complete Student, StudentFee, Batch and Course entities.
 * </p>
 */
public interface StudentFeeOutstandingReportProjection {

    Long getStudentId();

    String getStudentName();

    String getCourseName();

    String getBatchName();

    BigDecimal getTotalFee();

    BigDecimal getDiscount();

    BigDecimal getFinalFee();

    BigDecimal getPaidAmount();

    BigDecimal getPendingAmount();

    FeeStatus getFeeStatus();

    LocalDate getFeeDueDate();
}