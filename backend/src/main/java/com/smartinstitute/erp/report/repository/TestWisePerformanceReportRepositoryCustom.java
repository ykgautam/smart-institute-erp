package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.projection.TestWisePerformanceReportProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface TestWisePerformanceReportRepositoryCustom {

    Page<TestWisePerformanceReportProjection>
    getTestWisePerformanceReportWithSorting(
            Long instituteId,
            Long courseId,
            Long batchId,
            Long testId,
            LocalDateTime submittedFrom,
            LocalDateTime submittedTo,
            Pageable pageable,
            String sortBy,
            String sortDirection
    );
}