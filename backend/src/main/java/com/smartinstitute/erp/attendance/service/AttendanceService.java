package com.smartinstitute.erp.attendance.service;

import com.smartinstitute.erp.attendance.dto.request.MarkAttendanceRequest;
import com.smartinstitute.erp.attendance.dto.request.UpdateAttendanceRequest;
import com.smartinstitute.erp.attendance.dto.response.AttendanceMonthlyReportResponse;
import com.smartinstitute.erp.attendance.dto.response.AttendanceResponse;
import com.smartinstitute.erp.attendance.dto.response.AttendanceSummaryResponse;
import com.smartinstitute.erp.attendance.dto.response.BatchAttendanceReportResponse;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    List<AttendanceResponse> markAttendance(
            MarkAttendanceRequest request
    );

    AttendanceResponse updateAttendance(
            Long attendanceId,
            UpdateAttendanceRequest request
    );

    List<AttendanceResponse> getBatchAttendance(
            Long batchId,
            LocalDate attendanceDate
    );

    List<AttendanceResponse> getStudentAttendanceHistory(
            Long studentId
    );

    AttendanceSummaryResponse getAttendanceSummary(Long studentId);

    List<BatchAttendanceReportResponse> getBatchAttendanceReport(Long batchId);

    AttendanceMonthlyReportResponse getMonthlyAttendanceReport(
            Long studentId,
            Integer year,
            Integer month
    );

    List<BatchAttendanceReportResponse> getBatchAttendanceReport(
            Long batchId,
            LocalDate from,
            LocalDate to
    );

}