package com.smartinstitute.erp.report.service;

import com.smartinstitute.erp.report.dto.request.TestWisePerformanceReportRequest;
import com.smartinstitute.erp.report.dto.response.TestWisePerformanceReportPageResponse;

public interface TestWisePerformanceReportService {

    TestWisePerformanceReportPageResponse getTestWisePerformanceReport(
            TestWisePerformanceReportRequest request
    );
}