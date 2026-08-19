package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.projection.CoursePerformanceReportProjection;
import com.smartinstitute.erp.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoursePerformanceReportRepository
        extends JpaRepository<Course, Long>,
        CoursePerformanceReportRepositoryCustom {
}