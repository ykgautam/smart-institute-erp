package com.smartinstitute.erp.report.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.report.dto.request.StudentReportRequest;
import com.smartinstitute.erp.report.dto.response.StudentReportPageResponse;
import com.smartinstitute.erp.report.service.StudentReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/reports/students")
public class StudentReportController {

    private final StudentReportService studentReportService;

    public StudentReportController(
            StudentReportService studentReportService) {

        this.studentReportService = studentReportService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<StudentReportPageResponse>>
    getStudentReport(
            @ModelAttribute StudentReportRequest request) {

        StudentReportPageResponse response =
                studentReportService.getStudentReport(request);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        response,
                        "Student report fetched successfully."
                )
        );
    }


}