package com.smartinstitute.erp.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Student-related KPI information displayed on the admin dashboard.
 */
@Getter
@AllArgsConstructor
public class DashboardStudentSummaryResponse {

    private Long totalStudents;

    private Long activeStudents;
}