package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.dto.response.StudentFeeCollectionReportSummaryResponse;
import com.smartinstitute.erp.report.projection.StudentFeeCollectionReportProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Custom repository operations for the Student Fee Collection Report.
 */
public interface StudentFeeCollectionReportRepositoryCustom {

    /**
     * Fetches paginated student fee collection records.
     */
    Page<StudentFeeCollectionReportProjection>
    getStudentFeeCollectionReport(
            Long instituteId,
            Long courseId,
            Long batchId,
            Long studentId,
            String feeStatus,
            Pageable pageable,
            String sortBy,
            String sortDirection
    );

    /**
     * Fetches aggregate summary for the complete filtered dataset.
     */
    StudentFeeCollectionReportSummaryResponse getSummary(
            Long instituteId,
            Long courseId,
            Long batchId,
            Long studentId,
            String feeStatus
    );
}