package com.smartinstitute.erp.report.projection;

import com.smartinstitute.erp.common.enums.fee.FeeStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Projection used by the Student Fee Collection Report.
 *
 * <p>
 * Contains the current fee and payment snapshot for each student.
 * </p>
 */
public interface StudentFeeCollectionReportProjection {

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

    /**
     * Fee due date associated with the student's fee record.
     */
    LocalDate getFeeDueDate();
}