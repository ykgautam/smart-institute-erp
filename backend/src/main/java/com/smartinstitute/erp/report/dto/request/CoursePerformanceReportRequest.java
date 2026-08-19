package com.smartinstitute.erp.report.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CoursePerformanceReportRequest {

    private Long instituteId;

    private Long courseId;

    private LocalDateTime submittedFrom;

    private LocalDateTime submittedTo;

    private Integer page = 0;

    private Integer size = 10;

    private String sortBy = "courseName";

    private String sortDirection = "ASC";
}