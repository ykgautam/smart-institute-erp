package com.smartinstitute.erp.report.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StudentAcademicReportPageResponse {

    private List<StudentAcademicReportResponse> content;

    private StudentAcademicReportSummaryResponse summary;

    private long totalElements;

    private int totalPages;

    private int currentPage;

    private int pageSize;

    private boolean first;

    private boolean last;
}