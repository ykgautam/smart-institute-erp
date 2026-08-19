package com.smartinstitute.erp.report.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TestWisePerformanceReportRequest {

    private Long courseId;

    private Long batchId;

    private Long testId;

    private LocalDateTime submittedFrom;

    private LocalDateTime submittedTo;

    private Integer page = 0;

    private Integer size = 10;

    private String sortBy = "testName";

    private String sortDirection = "ASC";
}