package com.smartinstitute.erp.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Aggregate summary for the Student Attendance Report.
 *
 * <p>
 * Summary values are calculated from the complete filtered
 * attendance dataset and are independent of pagination.
 * </p>
 */
@Getter
@AllArgsConstructor
public class StudentAttendanceReportSummaryResponse {

    /**
     * Total number of unique students included in the
     * filtered attendance report.
     */
    private Long totalStudents;

    /**
     * Average attendance percentage across the students
     * included in the filtered report.
     */
    private BigDecimal averageAttendancePercentage;

    /**
     * Total number of attendance records/classes considered
     * across all students.
     */
    private Long totalClasses;

    /**
     * Total number of classes where students were present.
     */
    private Long totalPresentClasses;

    /**
     * Total number of classes where students were absent.
     */
    private Long totalAbsentClasses;
}