package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.projection.StudentTestPerformanceReportProjection;
import com.smartinstitute.erp.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentTestPerformanceReportRepository
        extends JpaRepository<Student, Long>,
        StudentTestPerformanceReportRepositoryCustom {
}