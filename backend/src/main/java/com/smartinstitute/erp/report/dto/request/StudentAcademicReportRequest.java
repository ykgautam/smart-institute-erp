package com.smartinstitute.erp.report.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentAcademicReportRequest {

    private Long courseId;

    private Long batchId;

    private Long studentId;

    private Integer page = 0;

    private Integer size = 10;

    private String sortBy = "studentName";

    private String sortDirection = "ASC";
}