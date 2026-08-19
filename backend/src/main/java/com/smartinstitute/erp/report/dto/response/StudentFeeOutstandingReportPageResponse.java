package com.smartinstitute.erp.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Paginated response for the Student Fee Outstanding Report.
 *
 * <p>
 * Contains report records along with pagination metadata so that
 * the frontend can easily implement server-side pagination.
 * </p>
 */
@Getter
@AllArgsConstructor
public class StudentFeeOutstandingReportPageResponse {

    /**
     * Report records for the current page.
     */
    private List<StudentFeeOutstandingReportResponse> content;

    /**
     * Overall report summary.
     *
     * <p>
     * This is calculated independently of pagination.
     * </p>
     */
    private StudentFeeOutstandingReportSummaryResponse summary;

    /**
     * Total number of matching outstanding-fee records.
     */
    private long totalElements;

    /**
     * Total number of pages.
     */
    private int totalPages;

    /**
     * Current zero-based page number.
     */
    private int currentPage;

    /**
     * Number of records requested per page.
     */
    private int pageSize;

    /**
     * Indicates whether this is the first page.
     */
    private boolean first;

    /**
     * Indicates whether this is the last page.
     */
    private boolean last;
}