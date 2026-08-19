package com.smartinstitute.erp.report.service;

import com.smartinstitute.erp.report.dto.request.StudentPerformanceReportRequest;
import com.smartinstitute.erp.report.dto.response.StudentPerformanceReportPageResponse;

public interface StudentPerformanceReportService {

    /**
     * Fetches paginated academic performance information
     * for students of the requested institute.
     *
     * <p>
     * Supports optional filtering by student, course, batch
     * and submission date range.
     * </p>
     *
     * @param request report filters and pagination/sorting information
     * @return paginated student performance report
     */
    StudentPerformanceReportPageResponse getStudentPerformanceReport(
            StudentPerformanceReportRequest request
    );
}