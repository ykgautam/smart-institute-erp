package com.smartinstitute.erp.report.dto.request;

import com.smartinstitute.erp.common.enums.test.StudentTestStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TestPerformanceReportRequest {

    private Long testId;

    private Long courseId;

    private Long batchId;

    private Long studentId;

    private StudentTestStatus status;

    private LocalDateTime submittedFrom;

    private LocalDateTime submittedTo;

    private Integer page = 0;

    private Integer size = 10;

    private String sortBy = "studentName";

    private String sortDirection = "asc";
}