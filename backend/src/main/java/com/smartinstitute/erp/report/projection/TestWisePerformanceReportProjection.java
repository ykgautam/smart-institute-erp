package com.smartinstitute.erp.report.projection;

import java.math.BigDecimal;

public interface TestWisePerformanceReportProjection {

    Long getTestId();

    String getTestName();

    String getCourseName();

    String getTopicName();

    Long getTotalAttempts();

    Long getSubmittedAttempts();

    Long getPassedAttempts();

    Long getFailedAttempts();

    BigDecimal getAveragePercentage();

    BigDecimal getHighestPercentage();

    BigDecimal getLowestPercentage();
}