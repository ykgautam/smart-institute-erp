package com.smartinstitute.erp.report.service;

import com.smartinstitute.erp.report.dto.request.CoursePerformanceReportRequest;
import com.smartinstitute.erp.report.dto.response.CoursePerformanceReportPageResponse;

/**
 * Service interface for the Course Performance Report.
 *
 * <p>
 * Provides course-level academic performance information
 * for institute administrators.
 * </p>
 */
public interface CoursePerformanceReportService {

    /**
     * Fetches paginated and sorted course performance data.
     *
     * @param request report filters, pagination and sorting parameters
     * @return paginated course performance report
     */
    CoursePerformanceReportPageResponse getCoursePerformanceReport(
            CoursePerformanceReportRequest request
    );
}