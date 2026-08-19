package com.smartinstitute.erp.report.projection;

import java.math.BigDecimal;

public interface AttendanceReportProjection {

    Long getStudentId();

    String getStudentName();

    String getCourseName();

    String getBatchName();

    Long getTotalClasses();

    Long getPresentClasses();

    Long getAbsentClasses();

    BigDecimal getAttendancePercentage();
}