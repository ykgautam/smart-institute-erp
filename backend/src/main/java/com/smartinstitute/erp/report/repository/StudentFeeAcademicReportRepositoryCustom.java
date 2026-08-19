package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.projection.StudentFeeAcademicReportProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentFeeAcademicReportRepositoryCustom {

    Page<StudentFeeAcademicReportProjection>
    getStudentFeeAcademicReportWithSorting(
            Long instituteId,
            Long courseId,
            Long batchId,
            Long studentId,
            Pageable pageable,
            String sortBy,
            String sortDirection
    );
}