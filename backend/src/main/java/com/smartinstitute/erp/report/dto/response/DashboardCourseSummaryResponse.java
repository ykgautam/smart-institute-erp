package com.smartinstitute.erp.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Course-related KPI information displayed on the admin dashboard.
 */
@Getter
@AllArgsConstructor
public class DashboardCourseSummaryResponse {

    private Long totalCourses;

    private Long activeCourses;
}