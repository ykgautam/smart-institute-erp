package com.smartinstitute.erp.report.projection;

import java.math.BigDecimal;

public interface StudentAcademicReportProjection {

    Long getStudentId();

    String getStudentName();

    String getCourseName();

    String getBatchName();

    BigDecimal getAttendancePercentage();

    Long getTotalTests();

    Long getTotalAttempts();

    Long getPassedTests();

    Long getFailedTests();

    BigDecimal getAverageTestPercentage();
}