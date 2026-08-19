package com.smartinstitute.erp.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestPerformanceReportResponse {

    private Long studentTestId;

    private Long studentId;

    private String studentName;

    private Long testId;

    private String testName;

    private String courseName;

    private String batchName;

    private Integer attemptNo;

    private String status;

    private Integer totalMarks;

    private Integer obtainedMarks;

    private BigDecimal percentage;

    private Boolean passed;

    private LocalDateTime startedAt;

    private LocalDateTime submittedAt;
}