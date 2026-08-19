package com.smartinstitute.erp.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestWisePerformanceReportResponse {

    private Long testId;

    private String testName;

    private String courseName;

    private String topicName;

    private Long totalAttempts;

    private Long submittedAttempts;

    private Long passedAttempts;

    private Long failedAttempts;

    private BigDecimal averagePercentage;

    private BigDecimal highestPercentage;

    private BigDecimal lowestPercentage;
}