package com.smartinstitute.erp.report.dto.request;

import com.smartinstitute.erp.common.enums.StudentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class StudentReportRequest {

    private Long courseId;

    private Long batchId;

    private StudentStatus status;

    private LocalDate admissionDateFrom;

    private LocalDate admissionDateTo;

    private Integer page = 0;

    private Integer size = 10;

    private String sortBy = "studentName";

    private String sortDirection = "asc";
}