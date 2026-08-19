package com.smartinstitute.erp.report.service;

import com.smartinstitute.erp.report.dto.request.AttendanceReportRequest;
import com.smartinstitute.erp.report.dto.response.AttendanceReportPageResponse;
import com.smartinstitute.erp.report.dto.response.AttendanceReportResponse;

import java.util.List;

public interface AttendanceReportService {

    AttendanceReportPageResponse getAttendanceReport(
            AttendanceReportRequest request
    );
}