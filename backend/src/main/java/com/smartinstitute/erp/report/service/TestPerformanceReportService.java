package com.smartinstitute.erp.report.service;

import com.smartinstitute.erp.report.dto.request.TestPerformanceReportRequest;
import com.smartinstitute.erp.report.dto.response.TestPerformanceReportPageResponse;

public interface TestPerformanceReportService {

    TestPerformanceReportPageResponse getTestPerformanceReport(
            TestPerformanceReportRequest request
    );
}