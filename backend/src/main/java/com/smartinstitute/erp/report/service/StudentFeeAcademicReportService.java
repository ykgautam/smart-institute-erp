package com.smartinstitute.erp.report.service;

import com.smartinstitute.erp.report.dto.request.StudentFeeAcademicReportRequest;
import com.smartinstitute.erp.report.dto.response.StudentFeeAcademicReportPageResponse;

public interface StudentFeeAcademicReportService {

    StudentFeeAcademicReportPageResponse
    getStudentFeeAcademicReport(
            StudentFeeAcademicReportRequest request
    );
}