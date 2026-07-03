package com.smartinstitute.erp.attendance.controller;

import com.smartinstitute.erp.attendance.dto.request.MarkAttendanceRequest;
import com.smartinstitute.erp.attendance.dto.request.UpdateAttendanceRequest;
import com.smartinstitute.erp.attendance.dto.response.AttendanceMonthlyReportResponse;
import com.smartinstitute.erp.attendance.dto.response.AttendanceResponse;
import com.smartinstitute.erp.attendance.dto.response.AttendanceSummaryResponse;
import com.smartinstitute.erp.attendance.dto.response.BatchAttendanceReportResponse;
import com.smartinstitute.erp.attendance.service.AttendanceService;
import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','FACULTY')")
    public ApiResponse<List<AttendanceResponse>> markAttendance(
            @Valid @RequestBody MarkAttendanceRequest request) {

        List<AttendanceResponse> response =
                attendanceService.markAttendance(request);

        return ApiResponseUtil.success(
                response,
                "Attendance marked successfully."
        );
    }

    @PutMapping("/{attendanceId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','FACULTY')")
    public ApiResponse<AttendanceResponse> updateAttendance(
            @PathVariable Long attendanceId,
            @Valid @RequestBody UpdateAttendanceRequest request) {

        AttendanceResponse response =
                attendanceService.updateAttendance(
                        attendanceId,
                        request
                );

        return ApiResponseUtil.success(
                response,
                "Attendance updated successfully."
        );
    }

    @GetMapping("/batch/{batchId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','FACULTY')")
    public ApiResponse<List<AttendanceResponse>> getBatchAttendance(
            @PathVariable Long batchId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate attendanceDate) {

        List<AttendanceResponse> response =
                attendanceService.getBatchAttendance(
                        batchId,
                        attendanceDate
                );

        return ApiResponseUtil.success(
                response,
                "Attendance fetched successfully."
        );
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','FACULTY')")
    public ApiResponse<List<AttendanceResponse>> getStudentAttendanceHistory(
            @PathVariable Long studentId) {

        List<AttendanceResponse> response =
                attendanceService.getStudentAttendanceHistory(
                        studentId
                );

        return ApiResponseUtil.success(
                response,
                "Student Attendance history fetched successfully."
        );
    }

    @GetMapping("/student/{studentId}/summary")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','FACULTY')")
    public ApiResponse<AttendanceSummaryResponse> getAttendanceSummary(
            @PathVariable Long studentId) {

        AttendanceSummaryResponse response =
                attendanceService.getAttendanceSummary(studentId);

        return ApiResponseUtil.success(response,
                "Attendance Summary is fetched successfully");
    }

    @GetMapping("/batch/{batchId}/report")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','FACULTY')")
    public ApiResponse<List<BatchAttendanceReportResponse>> getBatchAttendanceReport(
            @PathVariable Long batchId) {

        List<BatchAttendanceReportResponse> response =
                attendanceService.getBatchAttendanceReport(batchId);

        return ApiResponseUtil.success(response,
                "Batch Attendance Report is fetched successfully");
    }

    @GetMapping("/student/{studentId}/monthly/date-range")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','FACULTY')")
    public ApiResponse<AttendanceMonthlyReportResponse> getMonthlyAttendanceReport(

            @PathVariable Long studentId,

            @RequestParam Integer year,

            @RequestParam Integer month) {

        AttendanceMonthlyReportResponse response =
                attendanceService.getMonthlyAttendanceReport(
                        studentId,
                        year,
                        month
                );

        return ApiResponseUtil.success(
                response,
                "Monthly attendance report fetched successfully."
        );
    }

    @GetMapping("/batch/{batchId}/report/date-range")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','FACULTY')")
    public ApiResponse<List<BatchAttendanceReportResponse>> getBatchAttendanceReport(

            @PathVariable Long batchId,

            @RequestParam LocalDate from,

            @RequestParam LocalDate to) {

        List<BatchAttendanceReportResponse> response =
                attendanceService.getBatchAttendanceReport(
                        batchId,
                        from,
                        to
                );

        return ApiResponseUtil.success(
                response,
                "Batch attendance report fetched successfully."
        );
    }

}