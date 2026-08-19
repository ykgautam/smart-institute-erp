package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.dto.response.StudentAttendanceReportSummaryResponse;
import com.smartinstitute.erp.report.projection.StudentAttendanceReportProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

/**
 * Custom repository contract for the Student Attendance Report.
 *
 * <p>
 * Attendance reporting requires aggregate calculations such as
 * total classes, present classes, absent classes and attendance
 * percentage. These calculations are handled through a custom
 * reporting query.
 * </p>
 */
public interface StudentAttendanceReportRepositoryCustom {

    /**
     * Fetches paginated student attendance information.
     *
     * @param instituteId institute whose attendance data is reported
     * @param courseId optional course filter
     * @param batchId optional batch filter
     * @param studentId optional student filter
     * @param dateFrom optional minimum attendance date
     * @param dateTo optional maximum attendance date
     * @param pageable pagination information
     * @param sortBy whitelisted report sort field
     * @param sortDirection ASC or DESC
     * @return paginated student attendance report
     */
    Page<StudentAttendanceReportProjection>
    getStudentAttendanceReport(
            Long instituteId,
            Long courseId,
            Long batchId,
            Long studentId,
            LocalDate dateFrom,
            LocalDate dateTo,
            Pageable pageable,
            String sortBy,
            String sortDirection
    );

    /**
     * Calculates aggregate attendance statistics for the complete
     * filtered dataset.
     *
     * <p>
     * Pagination is intentionally not applied to this query.
     * Therefore the summary represents the complete filtered
     * result set rather than only the current page.
     * </p>
     *
     * @param instituteId institute whose attendance is being reported
     * @param courseId optional course filter
     * @param batchId optional batch filter
     * @param studentId optional student filter
     * @param attendanceDateFrom optional minimum attendance date
     * @param attendanceDateTo optional maximum attendance date
     * @return aggregate attendance summary
     */
    StudentAttendanceReportSummaryResponse getSummary(
            Long instituteId,
            Long courseId,
            Long batchId,
            Long studentId,
            LocalDate attendanceDateFrom,
            LocalDate attendanceDateTo
    );
}