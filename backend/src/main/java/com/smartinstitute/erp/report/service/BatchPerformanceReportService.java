package com.smartinstitute.erp.report.service;

import com.smartinstitute.erp.report.dto.request.BatchPerformanceReportRequest;
import com.smartinstitute.erp.report.dto.response.BatchPerformanceReportPageResponse;

public interface BatchPerformanceReportService {

    BatchPerformanceReportPageResponse getBatchPerformanceReport(
            BatchPerformanceReportRequest request
    );
}