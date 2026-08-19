package com.smartinstitute.erp.report.projection;

import java.math.BigDecimal;

public interface StudentFeeAcademicReportProjection {

    Long getStudentId();

    String getStudentName();

    String getCourseName();

    String getBatchName();

    BigDecimal getFinalFee();

    BigDecimal getPaidAmount();

    BigDecimal getPendingAmount();

    String getFeeStatus();

    BigDecimal getAttendancePercentage();

    Long getTotalTests();

    Long getTotalAttempts();

    Long getPassedTests();

    Long getFailedTests();

    BigDecimal getAverageTestPercentage();
}