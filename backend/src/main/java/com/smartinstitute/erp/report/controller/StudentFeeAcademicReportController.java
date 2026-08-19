package com.smartinstitute.erp.report.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.report.dto.request.StudentFeeAcademicReportRequest;
import com.smartinstitute.erp.report.dto.response.StudentFeeAcademicReportPageResponse;
import com.smartinstitute.erp.report.service.StudentFeeAcademicReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/reports/fees/academic")
public class StudentFeeAcademicReportController {

    private final StudentFeeAcademicReportService studentFeeAcademicReportService;

    public StudentFeeAcademicReportController(
            StudentFeeAcademicReportService studentFeeAcademicReportService) {

        this.studentFeeAcademicReportService =
                studentFeeAcademicReportService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<StudentFeeAcademicReportPageResponse>>
    getStudentFeeAcademicReport(
            @ModelAttribute StudentFeeAcademicReportRequest request) {

        StudentFeeAcademicReportPageResponse response =
                studentFeeAcademicReportService
                        .getStudentFeeAcademicReport(request);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        response,
                        "Student fee academic report fetched successfully."
                )
        );
    }
}