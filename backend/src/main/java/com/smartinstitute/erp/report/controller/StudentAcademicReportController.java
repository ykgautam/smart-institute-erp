package com.smartinstitute.erp.report.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.report.dto.request.StudentAcademicReportRequest;
import com.smartinstitute.erp.report.dto.response.StudentAcademicReportPageResponse;
import com.smartinstitute.erp.report.service.StudentAcademicReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/reports/students/academic")
public class StudentAcademicReportController {

    private final StudentAcademicReportService
            studentAcademicReportService;

    public StudentAcademicReportController(
            StudentAcademicReportService studentAcademicReportService) {

        this.studentAcademicReportService =
                studentAcademicReportService;
    }

    @GetMapping
    public ResponseEntity<
            ApiResponse<StudentAcademicReportPageResponse>>
    getStudentAcademicReport(
            @ModelAttribute StudentAcademicReportRequest request) {

        StudentAcademicReportPageResponse response =
                studentAcademicReportService
                        .getStudentAcademicReport(request);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        response,
                        "Student academic report fetched successfully."
                )
        );
    }
}