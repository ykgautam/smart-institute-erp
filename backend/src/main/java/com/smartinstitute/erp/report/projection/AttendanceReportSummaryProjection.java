package com.smartinstitute.erp.report.projection;

public interface AttendanceReportSummaryProjection {

    Long getTotalStudents();

    Long getTotalClasses();

    Long getTotalPresent();

    Long getTotalAbsent();

    java.math.BigDecimal getAverageAttendancePercentage();

    Long getLowAttendanceStudents();
}