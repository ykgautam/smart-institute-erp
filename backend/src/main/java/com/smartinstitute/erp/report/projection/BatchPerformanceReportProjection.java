package com.smartinstitute.erp.report.projection;

import java.math.BigDecimal;

public interface BatchPerformanceReportProjection {

    Long getBatchId();

    String getBatchName();

    String getCourseName();

    Long getTotalStudents();

    Long getStudentsAttempted();

    Long getTotalAttempts();

    Long getSubmittedAttempts();

    Long getPassedAttempts();

    Long getFailedAttempts();

    BigDecimal getAveragePercentage();

    BigDecimal getHighestPercentage();

    BigDecimal getLowestPercentage();
}