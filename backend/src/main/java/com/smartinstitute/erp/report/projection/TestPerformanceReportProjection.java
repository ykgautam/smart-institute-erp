package com.smartinstitute.erp.report.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TestPerformanceReportProjection {

    Long getStudentTestId();

    Long getStudentId();

    String getStudentName();

    Long getTestId();

    String getTestName();

    String getCourseName();

    String getBatchName();

    Integer getAttemptNo();

    String getStatus();

    Integer getTotalMarks();

    Integer getObtainedMarks();

    BigDecimal getPercentage();

    Boolean getPassed();

    LocalDateTime getStartedAt();

    LocalDateTime getSubmittedAt();
}