package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.projection.StudentFeeOutstandingReportProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

/**
 * Custom repository contract for Student Fee Outstanding Report.
 */
public interface StudentFeeOutstandingReportRepositoryCustom {

    /**
     * Fetches paginated outstanding fee records.
     *
     * @param instituteId institute whose data should be reported
     * @param courseId optional course filter
     * @param batchId optional batch filter
     * @param feeStatus optional fee status filter
     * @param dueDateFrom optional minimum due date
     * @param dueDateTo optional maximum due date
     * @param pageable pagination information
     * @param sortBy whitelisted report sort field
     * @param sortDirection ASC or DESC
     * @return paginated outstanding fee report
     */
    Page<StudentFeeOutstandingReportProjection>
    getStudentFeeOutstandingReport(
            Long instituteId,
            Long courseId,
            Long batchId,
            String feeStatus,
            LocalDate dueDateFrom,
            LocalDate dueDateTo,
            Pageable pageable,
            String sortBy,
            String sortDirection
    );

    /**
     * Calculates the overall summary for the filtered student population.
     *
     * <p>
     * Unlike the paginated report query, this query does not apply
     * the outstanding condition to the financial totals. Therefore
     * fully-paid students are also included in total fee, discount,
     * final fee and paid amount calculations.
     * </p>
     *
     * @param instituteId institute whose data should be reported
     * @param courseId optional course filter
     * @param batchId optional batch filter
     * @param feeStatus optional fee status filter
     * @param dueDateFrom optional minimum due date
     * @param dueDateTo optional maximum due date
     * @return summary values for the filtered student population
     */
    Object[]
    getStudentFeeOutstandingReportSummary(
            Long instituteId,
            Long courseId,
            Long batchId,
            String feeStatus,
            LocalDate dueDateFrom,
            LocalDate dueDateTo
    );
}