package com.smartinstitute.erp.report.service;

import com.smartinstitute.erp.report.dto.request.StudentFeeCollectionReportRequest;
import com.smartinstitute.erp.report.dto.response.StudentFeeCollectionReportPageResponse;

public interface StudentFeeCollectionReportService {

    /**
     * Generates the Student Fee Collection Report.
     *
     * <p>
     * The report provides paginated student-wise fee collection details
     * along with an aggregate summary for the complete filtered dataset.
     * </p>
     *
     * @param request report filters and pagination/sorting parameters
     * @return paginated fee collection report with summary
     */
    StudentFeeCollectionReportPageResponse getStudentFeeCollectionReport(
            StudentFeeCollectionReportRequest request);
    
}