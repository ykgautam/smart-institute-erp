package com.smartinstitute.erp.report.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.report.dto.request.CoursePerformanceReportRequest;
import com.smartinstitute.erp.report.dto.response.CoursePerformanceReportPageResponse;
import com.smartinstitute.erp.report.service.CoursePerformanceReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for the Course Performance Report.
 *
 * <p>
 * This API is used by institute administrators to view aggregated
 * academic performance at the course level.
 * </p>
 *
 * <p>
 * The report provides metrics such as:
 * </p>
 *
 * <ul>
 *     <li>Total students</li>
 *     <li>Students who attempted tests</li>
 *     <li>Total attempts</li>
 *     <li>Submitted attempts</li>
 *     <li>Passed attempts</li>
 *     <li>Failed attempts</li>
 *     <li>Average percentage</li>
 *     <li>Highest percentage</li>
 *     <li>Lowest percentage</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/admin/reports/courses")
public class CoursePerformanceReportController {

    private final CoursePerformanceReportService
            coursePerformanceReportService;

    public CoursePerformanceReportController(
            CoursePerformanceReportService coursePerformanceReportService) {

        this.coursePerformanceReportService =
                coursePerformanceReportService;
    }

    /**
     * Fetches the course-wise performance report.
     *
     * <p>
     * This endpoint allows administrators to analyze the academic
     * performance of students aggregated at the course level.
     * </p>
     *
     * <p>
     * Supported request features include:
     * </p>
     *
     * <ul>
     *     <li>Course filtering</li>
     *     <li>Submission date filtering</li>
     *     <li>Pagination</li>
     *     <li>Dynamic sorting</li>
     * </ul>
     *
     * <p>
     * Example:
     * </p>
     *
     * <pre>
     * GET /api/v1/admin/reports/courses/performance
     * </pre>
     *
     * @param request course performance report filters,
     *                pagination and sorting parameters
     * @return paginated course performance report
     */
    @GetMapping("/performance")
    public ResponseEntity<
            ApiResponse<CoursePerformanceReportPageResponse>>
    getCoursePerformanceReport(
            @ModelAttribute CoursePerformanceReportRequest request) {

        System.out.println("Course Performance instituteId = " + request.getInstituteId());
        CoursePerformanceReportPageResponse response =
                coursePerformanceReportService
                        .getCoursePerformanceReport(request);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        response,
                        "Course performance report fetched successfully."
                )
        );
    }
}