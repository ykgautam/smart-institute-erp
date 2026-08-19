package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.projection.StudentTestPerformanceReportProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface StudentTestPerformanceReportRepositoryCustom {

    Page<StudentTestPerformanceReportProjection>
    getStudentTestPerformanceReportWithSorting(
            Long instituteId,
            Long courseId,
            Long batchId,
            Long studentId,
            Long testId,
            LocalDateTime submittedFrom,
            LocalDateTime submittedTo,
            Pageable pageable,
            String sortBy,
            String sortDirection
    );
}