package com.smartinstitute.erp.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Main response object for the admin dashboard.
 *
 * <p>
 * This response contains high-level institute KPIs.
 * Additional dashboard sections such as fees, attendance
 * and academic performance can be added in later dashboard packs.
 * </p>
 */
@Getter
@AllArgsConstructor
public class DashboardResponse {

    private DashboardStudentSummaryResponse students;

    private DashboardCourseSummaryResponse courses;

    private DashboardBatchSummaryResponse batches;
}