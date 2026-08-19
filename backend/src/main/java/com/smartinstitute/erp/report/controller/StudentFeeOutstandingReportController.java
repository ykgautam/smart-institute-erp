package com.smartinstitute.erp.report.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.report.dto.request.StudentFeeOutstandingReportRequest;
import com.smartinstitute.erp.report.dto.response.StudentFeeOutstandingReportPageResponse;
import com.smartinstitute.erp.report.service.StudentFeeOutstandingReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for the Student Fee Outstanding Report.
 *
 * <p>
 * This controller exposes administrator APIs for identifying
 * students who have unpaid or partially paid fees.
 * </p>
 *
 * <p>
 * The report supports filtering by course, batch, fee status
 * and fee due-date range, along with pagination and sorting.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/admin/reports/fees")
public class StudentFeeOutstandingReportController {

    private final StudentFeeOutstandingReportService
            studentFeeOutstandingReportService;

    public StudentFeeOutstandingReportController(
            StudentFeeOutstandingReportService
                    studentFeeOutstandingReportService) {

        this.studentFeeOutstandingReportService =
                studentFeeOutstandingReportService;
    }

    /**
     * Fetches the Student Fee Outstanding Report.
     *
     * <p>
     * Purpose of this API:
     * </p>
     *
     * <ul>
     *     <li>Identify students with pending fees.</li>
     *     <li>View total, paid and pending amounts.</li>
     *     <li>Filter outstanding fees by course and batch.</li>
     *     <li>Filter by fee status and due-date range.</li>
     *     <li>Support pagination and sorting for the admin UI.</li>
     * </ul>
     *
     * <p>
     * Endpoint:
     * {@code GET /api/v1/admin/reports/fees/outstanding}
     * </p>
     */
    @GetMapping("/outstanding")
    public ResponseEntity<
            ApiResponse<StudentFeeOutstandingReportPageResponse>>
    getStudentFeeOutstandingReport(
            @ModelAttribute StudentFeeOutstandingReportRequest request) {

        StudentFeeOutstandingReportPageResponse response =
                studentFeeOutstandingReportService
                        .getStudentFeeOutstandingReport(request);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        response,
                        "Student fee outstanding report fetched successfully."
                )
        );
    }
}