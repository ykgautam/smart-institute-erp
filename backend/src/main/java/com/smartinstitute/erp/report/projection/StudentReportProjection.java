package com.smartinstitute.erp.report.projection;

public interface StudentReportProjection {

    Long getStudentId();

    String getStudentName();

    String getEmail();

    String getCourseName();

    String getBatchName();

    String getStatus();
}