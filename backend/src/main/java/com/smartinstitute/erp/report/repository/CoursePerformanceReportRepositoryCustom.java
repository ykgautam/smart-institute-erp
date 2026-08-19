package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.projection.CoursePerformanceReportProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface CoursePerformanceReportRepositoryCustom {

    Page<CoursePerformanceReportProjection>
    getCoursePerformanceReportWithSorting(
            Long instituteId,
            Long courseId,
            LocalDateTime submittedFrom,
            LocalDateTime submittedTo,
            Pageable pageable,
            String sortBy,
            String sortDirection
    );
}