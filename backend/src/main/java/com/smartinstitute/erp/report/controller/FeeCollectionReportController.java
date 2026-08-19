package com.smartinstitute.erp.report.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.report.dto.request.FeeCollectionReportRequest;
import com.smartinstitute.erp.report.dto.response.FeeCollectionReportPageResponse;
import com.smartinstitute.erp.report.service.FeeCollectionReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/reports/fees")
public class FeeCollectionReportController {

    private final FeeCollectionReportService feeCollectionReportService;

    public FeeCollectionReportController(
            FeeCollectionReportService feeCollectionReportService) {

        this.feeCollectionReportService =
                feeCollectionReportService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<FeeCollectionReportPageResponse>>
    getFeeCollectionReport(
            @ModelAttribute FeeCollectionReportRequest request) {

        FeeCollectionReportPageResponse response =
                feeCollectionReportService
                        .getFeeCollectionReport(request);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        response,
                        "Fee collection report fetched successfully."
                )
        );
    }
}