package com.smartinstitute.erp.report.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.report.dto.request.StudentAttendanceReportRequest;
import com.smartinstitute.erp.report.dto.response.StudentAttendanceReportPageResponse;
import com.smartinstitute.erp.report.service.StudentAttendanceReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for Student Attendance Reports.
 *
 * <p>
 * Provides administrator-level APIs for viewing attendance performance
 * of students across courses and batches.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/admin/reports/students")
public class StudentAttendanceReportController {

    private final StudentAttendanceReportService studentAttendanceReportService;

    public StudentAttendanceReportController(
            StudentAttendanceReportService studentAttendanceReportService) {

        this.studentAttendanceReportService =
                studentAttendanceReportService;
    }

    /**
     * Fetches a paginated student attendance report.
     *
     * <p>
     * This API is used by administrators to analyze student attendance
     * across courses and batches. The report supports filters such as
     * course, batch, student and attendance date range, along with
     * pagination and sorting.
     * </p>
     *
     * <p>
     * Institute context is determined from the authenticated user
     * by the service layer. The client should not provide an
     * institute ID as a trusted value.
     * </p>
     *
     * @param request attendance report filters, pagination and sorting
     * @return paginated student attendance report
     */
    @GetMapping("/attendance")
    public ResponseEntity<ApiResponse<StudentAttendanceReportPageResponse>>
    getStudentAttendanceReport(
            @ModelAttribute StudentAttendanceReportRequest request) {

        StudentAttendanceReportPageResponse response =
                studentAttendanceReportService
                        .getStudentAttendanceReport(request);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        response,
                        "Student attendance report fetched successfully."
                )
        );
    }
}