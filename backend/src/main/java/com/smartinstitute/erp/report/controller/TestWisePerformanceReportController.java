package com.smartinstitute.erp.report.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.report.dto.request.TestWisePerformanceReportRequest;
import com.smartinstitute.erp.report.dto.response.TestWisePerformanceReportPageResponse;
import com.smartinstitute.erp.report.service.TestWisePerformanceReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/reports/tests/summary")
public class TestWisePerformanceReportController {

    private final TestWisePerformanceReportService
            testWisePerformanceReportService;

    public TestWisePerformanceReportController(
            TestWisePerformanceReportService
                    testWisePerformanceReportService) {

        this.testWisePerformanceReportService =
                testWisePerformanceReportService;
    }

    @GetMapping
    public ResponseEntity<
            ApiResponse<TestWisePerformanceReportPageResponse>>
    getTestWisePerformanceReport(
            @ModelAttribute TestWisePerformanceReportRequest request) {

        TestWisePerformanceReportPageResponse response =
                testWisePerformanceReportService
                        .getTestWisePerformanceReport(request);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        response,
                        "Test-wise performance report fetched successfully."
                )
        );
    }
}