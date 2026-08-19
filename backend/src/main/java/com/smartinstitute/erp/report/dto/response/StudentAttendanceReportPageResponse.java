package com.smartinstitute.erp.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Paginated response for the Student Attendance Report.
 *
 * <p>
 * This DTO contains the attendance records for the current page
 * along with pagination metadata.
 * </p>
 */
@Getter
@AllArgsConstructor
public class StudentAttendanceReportPageResponse {

    /**
     * Attendance records for the current page.
     */
    private List<StudentAttendanceReportResponse> content;

    /**
     * Aggregate summary calculated from the complete
     * filtered dataset.
     */
    private StudentAttendanceReportSummaryResponse summary;

    /**
     * Total number of records matching the applied filters.
     */
    private long totalElements;

    /**
     * Total number of available pages.
     */
    private int totalPages;

    /**
     * Zero-based current page number.
     */
    private int currentPage;

    /**
     * Number of records requested per page.
     */
    private int pageSize;

    /**
     * Indicates whether the current page is the first page.
     */
    private boolean first;

    /**
     * Indicates whether the current page is the last page.
     */
    private boolean last;
}