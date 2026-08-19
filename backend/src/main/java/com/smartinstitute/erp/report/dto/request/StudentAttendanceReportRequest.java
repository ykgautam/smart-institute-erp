package com.smartinstitute.erp.report.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Request filters for the Student Attendance Report.
 *
 * <p>
 * This request allows administrators to filter student attendance
 * information by course, batch, student and attendance date range.
 * </p>
 *
 * <p>
 * Pagination and sorting parameters are included because attendance
 * reports can contain a large number of students.
 * </p>
 */
@Getter
@Setter
public class StudentAttendanceReportRequest {

    /**
     * Optional course filter.
     *
     * <p>
     * When provided, only students belonging to the selected course
     * are included in the report.
     * </p>
     */
    private Long courseId;

    /**
     * Optional batch filter.
     *
     * <p>
     * When provided, only students belonging to the selected batch
     * are included in the report.
     * </p>
     */
    private Long batchId;

    /**
     * Optional student filter.
     *
     * <p>
     * When provided, the report is generated only for the selected
     * student.
     * </p>
     */
    private Long studentId;

    /**
     * Optional minimum attendance date.
     *
     * <p>
     * When provided, attendance records before this date are excluded.
     * </p>
     */
    private LocalDate dateFrom;

    /**
     * Optional maximum attendance date.
     *
     * <p>
     * When provided, attendance records after this date are excluded.
     * </p>
     */
    private LocalDate dateTo;

    /**
     * Zero-based page number.
     */
    @Min(0)
    private Integer page = 0;

    /**
     * Number of records per page.
     */
    @Min(1)
    @Max(100)
    private Integer size = 10;

    /**
     * Field used for sorting.
     *
     * <p>
     * The repository resolves this through a predefined whitelist.
     * </p>
     */
    private String sortBy;

    /**
     * Sort direction.
     *
     * <p>
     * Supported values are ASC and DESC.
     * </p>
     */
    private String sortDirection;
}