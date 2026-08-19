package com.smartinstitute.erp.report.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class StudentReportSummaryResponse {

    private long totalStudents;

    private Map<String, Long> statusCounts;
}