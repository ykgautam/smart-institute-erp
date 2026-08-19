package com.smartinstitute.erp.report.projection;

import java.math.BigDecimal;

/**
 * Projection used by the Student Attendance Report.
 *
 * <p>
 * This projection represents the aggregated attendance information
 * returned by the reporting query.
 * </p>
 *
 * <p>
 * Repository projections are kept separate from API response DTOs
 * so that database/reporting structures are not directly exposed
 * through the REST API.
 * </p>
 */
public interface StudentAttendanceReportProjection {

    /**
     * Unique identifier of the student.
     */
    Long getStudentId();

    /**
     * Full name of the student.
     */
    String getStudentName();

    /**
     * Course associated with the student's batch.
     */
    String getCourseName();

    /**
     * Batch associated with the student.
     */
    String getBatchName();

    /**
     * Total number of attendance records considered
     * for the selected date range.
     */
    Long getTotalClasses();

    /**
     * Number of classes where the student was marked PRESENT.
     */
    Long getPresentClasses();

    /**
     * Number of classes where the student was marked ABSENT.
     */
    Long getAbsentClasses();

    /**
     * Attendance percentage calculated from present classes
     * and total classes.
     */
    BigDecimal getAttendancePercentage();
}