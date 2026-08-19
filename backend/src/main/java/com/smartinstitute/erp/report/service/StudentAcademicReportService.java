package com.smartinstitute.erp.report.service;

import com.smartinstitute.erp.report.dto.request.StudentAcademicReportRequest;
import com.smartinstitute.erp.report.dto.response.StudentAcademicReportPageResponse;

public interface StudentAcademicReportService {

    StudentAcademicReportPageResponse
    getStudentAcademicReport(
            StudentAcademicReportRequest request
    );
}