package com.smartinstitute.erp.report.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class StudentAcademicReportResponse {

    private Long studentId;

    private String studentName;

    private String courseName;

    private String batchName;

    private BigDecimal attendancePercentage;

    private Long totalTests;

    private Long totalAttempts;

    private Long passedTests;

    private Long failedTests;

    private BigDecimal averageTestPercentage;
}