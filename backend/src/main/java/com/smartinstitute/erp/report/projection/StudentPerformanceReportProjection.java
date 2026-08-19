package com.smartinstitute.erp.report.projection;

import java.math.BigDecimal;

public interface StudentPerformanceReportProjection {

    Long getStudentId();

    String getStudentName();

    String getCourseName();

    String getBatchName();

    Long getTotalAttempts();

    Long getSubmittedAttempts();

    Long getPassedAttempts();

    Long getFailedAttempts();

    BigDecimal getAveragePercentage();

    BigDecimal getHighestPercentage();

    BigDecimal getLowestPercentage();
}