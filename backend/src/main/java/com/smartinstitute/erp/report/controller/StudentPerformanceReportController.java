package com.smartinstitute.erp.report.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;

import com.smartinstitute.erp.report.dto.request.StudentPerformanceReportRequest;
import com.smartinstitute.erp.report.dto.response.StudentPerformanceReportPageResponse;
import com.smartinstitute.erp.report.service.StudentPerformanceReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for student academic performance reports.
 *
 * <p>
 * These APIs are intended for institute administrators to analyze
 * individual student test performance, including attempts, pass/fail
 * statistics and percentage-based performance.
 * </p>
 *
 * <p>
 * The controller is responsible only for:
 * </p>
 * <ul>
 *     <li>Receiving HTTP requests.</li>
 *     <li>Accepting report filters and pagination parameters.</li>
 *     <li>Delegating business processing to the service layer.</li>
 *     <li>Returning the standard API response.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/admin/reports/students")
@RequiredArgsConstructor
public class StudentPerformanceReportController {

    private final StudentPerformanceReportService
            studentPerformanceReportService;

    /**
     * Fetches student academic performance report.
     *
     * <p>
     * This API provides administrators with student-level academic
     * performance information such as:
     * </p>
     *
     * <ul>
     *     <li>Total test attempts</li>
     *     <li>Submitted attempts</li>
     *     <li>Passed attempts</li>
     *     <li>Failed attempts</li>
     *     <li>Average percentage</li>
     *     <li>Highest percentage</li>
     *     <li>Lowest percentage</li>
     * </ul>
     *
     * <p>
     * The report supports pagination, sorting and optional filtering
     * by student, course, batch and submission date range.
     * </p>
     *
     * @param request student performance report filters
     * @return paginated student performance report
     */
    @GetMapping("/performance")
    public ResponseEntity<ApiResponse<StudentPerformanceReportPageResponse>>
    getStudentPerformanceReport(
            @ModelAttribute StudentPerformanceReportRequest request) {

        StudentPerformanceReportPageResponse response =
                studentPerformanceReportService
                        .getStudentPerformanceReport(request);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        response,
                        "Student performance report fetched successfully."
                )
        );
    }
}