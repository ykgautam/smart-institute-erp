package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.projection.TestPerformanceReportProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface TestPerformanceReportRepositoryCustom {

    Page<TestPerformanceReportProjection> getTestPerformanceReportWithSorting(
            Long instituteId,
            Long testId,
            Long courseId,
            Long batchId,
            Long studentId,
            String status,
            LocalDateTime submittedFrom,
            LocalDateTime submittedTo,
            Pageable pageable,
            String sortColumn,
            String sortDirection
    );
}