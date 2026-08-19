package com.smartinstitute.erp.report.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Request DTO for the student performance report.
 *
 * <p>
 * This DTO contains the filters, pagination and sorting parameters
 * required to generate the student-level academic performance report.
 * </p>
 */
@Getter
@Setter
public class StudentPerformanceReportRequest {

    /**
     * Institute for which the report is generated.
     *
     * <p>
     * This value should normally come from the authenticated
     * administrator's institute context.
     * </p>
     */
    private Long instituteId;

    /**
     * Optional student filter.
     */
    private Long studentId;

    /**
     * Optional course filter.
     */
    private Long courseId;

    /**
     * Optional batch filter.
     */
    private Long batchId;

    /**
     * Include attempts submitted from this date/time.
     */
    private LocalDateTime submittedFrom;

    /**
     * Include attempts submitted up to this date/time.
     */
    private LocalDateTime submittedTo;

    /**
     * Page number.
     *
     * <p>
     * Zero-based page index.
     * </p>
     */
    private int page = 0;

    /**
     * Number of records per page.
     */
    private int size = 10;

    /**
     * Field used for sorting.
     */
    private String sortBy = "studentName";

    /**
     * Sorting direction.
     *
     * <p>
     * Supported values are ASC and DESC.
     * </p>
     */
    private String sortDirection = "ASC";
}