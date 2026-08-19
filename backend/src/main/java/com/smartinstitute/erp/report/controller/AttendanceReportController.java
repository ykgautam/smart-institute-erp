package com.smartinstitute.erp.report.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.report.dto.request.AttendanceReportRequest;
import com.smartinstitute.erp.report.dto.response.AttendanceReportPageResponse;
import com.smartinstitute.erp.report.service.AttendanceReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/reports/attendance")
public class AttendanceReportController {

    private final AttendanceReportService attendanceReportService;

    public AttendanceReportController(
            AttendanceReportService attendanceReportService) {

        this.attendanceReportService =
                attendanceReportService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<AttendanceReportPageResponse>>
    getAttendanceReport(
            @ModelAttribute AttendanceReportRequest request) {

        AttendanceReportPageResponse response =
                attendanceReportService.getAttendanceReport(request);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        response,
                        "Attendance report fetched successfully."
                )
        );
    }
}