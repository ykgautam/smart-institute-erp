package com.smartinstitute.erp.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class BatchPerformanceReportResponse {

    private Long batchId;

    private String batchName;

    private String courseName;

    private Long totalStudents;

    private Long totalAttempts;

    private Long submittedAttempts;

    private Long passedAttempts;

    private Long failedAttempts;

    private BigDecimal averagePercentage;

    private Long studentsAttempted;
}