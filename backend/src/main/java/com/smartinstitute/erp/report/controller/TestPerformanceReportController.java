package com.smartinstitute.erp.report.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.report.dto.request.TestPerformanceReportRequest;
import com.smartinstitute.erp.report.dto.response.TestPerformanceReportPageResponse;
import com.smartinstitute.erp.report.service.TestPerformanceReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/reports/tests")
public class TestPerformanceReportController {

    private final TestPerformanceReportService
            testPerformanceReportService;

    public TestPerformanceReportController(
            TestPerformanceReportService
                    testPerformanceReportService) {

        this.testPerformanceReportService =
                testPerformanceReportService;
    }

    @GetMapping
    public ResponseEntity<
            ApiResponse<TestPerformanceReportPageResponse>>
    getTestPerformanceReport(
            @ModelAttribute TestPerformanceReportRequest request) {

        TestPerformanceReportPageResponse response =
                testPerformanceReportService
                        .getTestPerformanceReport(request);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        response,
                        "Test performance report fetched successfully."
                )
        );
    }
}