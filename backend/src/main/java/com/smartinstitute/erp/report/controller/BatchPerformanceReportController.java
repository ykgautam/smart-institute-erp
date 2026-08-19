package com.smartinstitute.erp.report.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.report.dto.request.BatchPerformanceReportRequest;
import com.smartinstitute.erp.report.dto.response.BatchPerformanceReportPageResponse;
import com.smartinstitute.erp.report.service.BatchPerformanceReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/reports/batches")
public class BatchPerformanceReportController {

    private final BatchPerformanceReportService batchPerformanceReportService;

    public BatchPerformanceReportController(
            BatchPerformanceReportService batchPerformanceReportService) {

        this.batchPerformanceReportService =
                batchPerformanceReportService;
    }

    @GetMapping("/performance")
    public ResponseEntity<ApiResponse<BatchPerformanceReportPageResponse>>
    getBatchPerformanceReport(
            @ModelAttribute BatchPerformanceReportRequest request) {

        BatchPerformanceReportPageResponse response =
                batchPerformanceReportService
                        .getBatchPerformanceReport(request);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        response,
                        "Batch performance report fetched successfully."
                )
        );
    }
}