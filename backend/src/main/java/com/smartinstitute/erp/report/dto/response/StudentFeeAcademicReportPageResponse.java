package com.smartinstitute.erp.report.dto.response;

import com.smartinstitute.erp.report.projection.StudentFeeAcademicReportProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StudentFeeAcademicReportPageResponse {

    private List<StudentFeeAcademicReportProjection> content;

    private long totalElements;

    private int totalPages;

    private int currentPage;

    private int pageSize;

    private boolean first;

    private boolean last;
}