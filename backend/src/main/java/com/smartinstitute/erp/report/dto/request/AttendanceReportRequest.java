package com.smartinstitute.erp.report.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class AttendanceReportRequest {

    private Long courseId;

    private Long batchId;

    private LocalDate attendanceDateFrom;

    private LocalDate attendanceDateTo;

    private Integer page = 0;

    private Integer size = 10;

    private String sortBy = "studentName";

    private String sortDirection = "ASC";
}