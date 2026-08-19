package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.projection.StudentPerformanceReportProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

/**
 * Repository contract for student performance reports.
 *
 * <p>
 * Provides student-level academic performance data with support for
 * filtering, pagination and sorting.
 * </p>
 */
public interface StudentPerformanceReportRepository {

    /**
     * Fetches the student performance report.
     *
     * @param instituteId institute/tenant identifier
     * @param studentId optional student filter
     * @param courseId optional course filter
     * @param batchId optional batch filter
     * @param submittedFrom optional submission start date/time
     * @param submittedTo optional submission end date/time
     * @param pageable pagination configuration
     * @param sortBy requested sort field
     * @param sortDirection ASC or DESC
     * @return paginated student performance report
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