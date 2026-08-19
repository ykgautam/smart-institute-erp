package com.smartinstitute.erp.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class StudentTestPerformanceReportResponse {

    private Long studentId;

    private String studentName;

    private String courseName;

    private String batchName;

    private Long totalAttempts;

    private Long submittedAttempts;

    private Long passedAttempts;

    private Long failedAttempts;

    private BigDecimal averagePercentage;

    private BigDecimal highestPercentage;

    private BigDecimal lowestPercentage;
}