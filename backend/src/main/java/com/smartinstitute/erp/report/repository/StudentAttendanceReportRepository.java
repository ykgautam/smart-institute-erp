package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for Student Attendance Report operations.
 *
 * <p>
 * The standard JpaRepository functionality is retained for
 * consistency with the project's repository architecture, while
 * report-specific aggregation is implemented through
 * {@link StudentAttendanceReportRepositoryCustom}.
 * </p>
 */
public interface StudentAttendanceReportRepository
        extends JpaRepository<Attendance, Long>,
                StudentAttendanceReportRepositoryCustom {
}