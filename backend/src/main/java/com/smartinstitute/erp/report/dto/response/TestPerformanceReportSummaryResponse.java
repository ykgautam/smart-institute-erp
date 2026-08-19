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
public class TestPerformanceReportSummaryResponse {

    private long totalAttempts;

    private long submittedAttempts;

    private long autoSubmittedAttempts;

    private long inProgressAttempts;

    private long passedAttempts;

    private long failedAttempts;

    private BigDecimal averagePercentage;

    private long totalMarks;

    private long totalObtainedMarks;
}