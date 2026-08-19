package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.projection.BatchPerformanceReportProjection;
import com.smartinstitute.erp.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchPerformanceReportRepository
        extends JpaRepository<Student, Long>,
        BatchPerformanceReportRepositoryCustom {
}