package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.projection.StudentPerformanceReportProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

/**
 * Custom repository contract for student performance reports.
 *
 * <p>
 * Contains the native-query based reporting operation that supports
 * filtering, pagination and dynamic sorting.
 * </p>
 */
public interface StudentPerformanceReportRepositoryCustom {

    /**
     * Fetches student performance information with optional filters.
     *
     * @param instituteId    institute/tenant identifier
     * @param studentId      optional student filter
     * @param courseId       optional course filter
     * @param batchId        optional batch filter
     * @param submittedFrom  optional submission start date/time
     * @param submittedTo    optional submission end date/time
     * @param pageable       pagination information
     * @param sortBy         requested sort field
     * @param sortDirection  requested sort direction
     * @return paginated student performance projections
     */
    Page<StudentPerformanceReportProjection>
    getStudentPerformanceReportWithSorting(
            Long instituteId,
            Long studentId,
            Long courseId,
            Long batchId,
            LocalDateTime submittedFrom,
            LocalDateTime submittedTo,
            Pageable pageable,
            String sortBy,
            String sortDirection
    );
}