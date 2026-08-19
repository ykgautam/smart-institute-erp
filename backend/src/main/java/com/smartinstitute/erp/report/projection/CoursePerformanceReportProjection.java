package com.smartinstitute.erp.report.projection;

import java.math.BigDecimal;

/**
 * Projection for course-wise performance report.
 *
 * <p>
 * Provides aggregated student and test-performance metrics
 * at the course level for the admin reporting module.
 * </p>
 */
public interface CoursePerformanceReportProjection {

    /**
     * Unique course identifier.
     */
    Long getCourseId();

    /**
     * Course display name.
     */
    String getCourseName();

    /**
     * Total number of active students enrolled
     * through batches belonging to the course.
     */
    Long getTotalStudents();

    /**
     * Number of distinct students who have submitted
     * or auto-submitted at least one test attempt.
     */
    Long getStudentsAttempted();

    /**
     * Total number of test attempts for the course.
     */
    Long getTotalAttempts();

    /**
     * Number of submitted or auto-submitted test attempts.
     */
    Long getSubmittedAttempts();

    /**
     * Number of submitted attempts where the student passed.
     */
    Long getPassedAttempts();

    /**
     * Number of submitted attempts where the student failed.
     */
    Long getFailedAttempts();

    /**
     * Average percentage across submitted test attempts.
     */
    BigDecimal getAveragePercentage();

    /**
     * Highest percentage achieved in a submitted test attempt.
     */
    BigDecimal getHighestPercentage();

    /**
     * Lowest percentage achieved in a submitted test attempt.
     */
    BigDecimal getLowestPercentage();
}