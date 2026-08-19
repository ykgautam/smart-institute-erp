package com.smartinstitute.erp.report.service;

import com.smartinstitute.erp.report.dto.request.FeeCollectionReportRequest;
import com.smartinstitute.erp.report.dto.response.FeeCollectionReportPageResponse;
import com.smartinstitute.erp.report.dto.response.FeeCollectionReportResponse;

import java.util.List;

public interface FeeCollectionReportService {

    FeeCollectionReportPageResponse getFeeCollectionReport(
            FeeCollectionReportRequest request
    );
}