package com.smartinstitute.erp.report.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class StudentAcademicReportSummaryResponse {

    private Long totalStudents;

    private BigDecimal averageAttendancePercentage;

    private Long totalTests;

    private Long totalAttempts;

    private Long passedTests;

    private Long failedTests;

    private BigDecimal averageTestPercentage;
}