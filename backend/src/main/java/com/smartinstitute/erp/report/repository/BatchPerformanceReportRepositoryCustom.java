package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.projection.BatchPerformanceReportProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface BatchPerformanceReportRepositoryCustom {

    Page<BatchPerformanceReportProjection>
    getBatchPerformanceReportWithSorting(
            Long instituteId,
            Long courseId,
            Long batchId,
            LocalDateTime submittedFrom,
            LocalDateTime submittedTo,
            Pageable pageable,
            String sortBy,
            String sortDirection
    );
}