package com.smartinstitute.erp.report.projection;

import java.math.BigDecimal;

public interface TestPerformanceReportSummaryProjection {

    Long getTotalAttempts();

    Long getSubmittedAttempts();

    Long getAutoSubmittedAttempts();

    Long getInProgressAttempts();

    Long getPassedAttempts();

    Long getFailedAttempts();

    BigDecimal getAveragePercentage();

    Long getTotalMarks();

    Long getTotalObtainedMarks();
}