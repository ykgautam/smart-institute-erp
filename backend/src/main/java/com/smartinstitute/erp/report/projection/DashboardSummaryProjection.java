package com.smartinstitute.erp.report.projection;

/**
 * Projection containing high-level KPI values required by
 * the admin dashboard.
 */
public interface DashboardSummaryProjection {

    Long getTotalStudents();

    Long getActiveStudents();

    Long getTotalCourses();

    Long getActiveCourses();

    Long getTotalBatches();

    Long getActiveBatches();
}