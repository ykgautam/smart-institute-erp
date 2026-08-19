package com.smartinstitute.erp.report.service;

import com.smartinstitute.erp.report.dto.request.StudentFeeOutstandingReportRequest;
import com.smartinstitute.erp.report.dto.response.StudentFeeOutstandingReportPageResponse;

/**
 * Service contract for Student Fee Outstanding Report.
 */
public interface StudentFeeOutstandingReportService {

    /**
     * Generates the paginated Student Fee Outstanding Report
     * together with its overall summary.
     *
     * @param request report filters and pagination/sorting information
     * @return paginated report with summary
     */
    StudentFeeOutstandingReportPageResponse
    getStudentFeeOutstandingReport(
            StudentFeeOutstandingReportRequest request
    );
}