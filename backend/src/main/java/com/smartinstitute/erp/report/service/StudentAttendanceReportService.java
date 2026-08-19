package com.smartinstitute.erp.report.service;

import com.smartinstitute.erp.report.dto.request.StudentAttendanceReportRequest;
import com.smartinstitute.erp.report.dto.response.StudentAttendanceReportPageResponse;

/**
 * Service contract for the Student Attendance Report.
 *
 * <p>
 * Provides student-wise attendance information for administrators.
 * The report supports filtering, pagination and sorting.
 * </p>
 */
public interface StudentAttendanceReportService {

    /**
     * Fetches the student attendance report.
     *
     * <p>
     * The institute is determined from the authenticated user's
     * current institute context. It is not accepted from the client.
     * </p>
     *
     * @param request attendance report filters, pagination and sorting
     * @return paginated student attendance report
     */
    StudentAttendanceReportPageResponse
    getStudentAttendanceReport(
            StudentAttendanceReportRequest request
    );
}