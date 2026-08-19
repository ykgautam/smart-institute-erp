package com.smartinstitute.erp.report.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.report.dto.request.StudentFeeCollectionReportRequest;
import com.smartinstitute.erp.report.dto.response.StudentFeeCollectionReportPageResponse;
import com.smartinstitute.erp.report.service.StudentFeeCollectionReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for the Student Fee Collection Report.
 *
 * <p>
 * Provides administrators with a paginated and sortable view of
 * student fee collection data, along with an aggregate summary
 * for the complete filtered dataset.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/admin/reports/fees")
public class StudentFeeCollectionReportController {

    private final StudentFeeCollectionReportService
            studentFeeCollectionReportService;

    public StudentFeeCollectionReportController(
            StudentFeeCollectionReportService
                    studentFeeCollectionReportService) {

        this.studentFeeCollectionReportService =
                studentFeeCollectionReportService;
    }

    /**
     * Fetches the Student Fee Collection Report.
     *
     * <p>
     * Supports optional course, batch, student and fee-status
     * filters along with pagination and sorting.
     * </p>
     *
     * <p>
     * The response contains:
     * </p>
     *
     * <ul>
     *     <li>Paginated student fee collection records</li>
     *     <li>Aggregate summary for the complete filtered dataset</li>
     *     <li>Pagination metadata</li>
     * </ul>
     *
     * @param request report filters, pagination and sorting parameters
     * @return student fee collection report
     */
    @GetMapping("/collection")
    public ResponseEntity<
            ApiResponse<StudentFeeCollectionReportPageResponse>>
    getStudentFeeCollectionReport(
            @ModelAttribute StudentFeeCollectionReportRequest request) {

        StudentFeeCollectionReportPageResponse response =
                studentFeeCollectionReportService
                        .getStudentFeeCollectionReport(request);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        response,
                        "Student fee collection report fetched successfully."
                )
        );
    }
}