package com.smartinstitute.erp.report.projection;

import java.math.BigDecimal;

public interface StudentAcademicReportSummaryProjection {

    Long getTotalStudents();

    BigDecimal getAverageAttendancePercentage();

    Long getTotalTests();

    Long getTotalAttempts();

    Long getPassedTests();

    Long getFailedTests();

    BigDecimal getAverageTestPercentage();
}