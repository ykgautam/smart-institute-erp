package com.smartinstitute.erp.report.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentFeeAcademicReportRequest {

    private int page = 0;

    private int size = 10;

    private Long courseId;

    private Long batchId;

    private Long studentId;

    private String sortBy = "studentName";

    private String sortDirection = "ASC";
}