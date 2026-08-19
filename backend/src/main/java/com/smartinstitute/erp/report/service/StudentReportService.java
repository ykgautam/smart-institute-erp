package com.smartinstitute.erp.report.service;

import com.smartinstitute.erp.report.dto.request.StudentReportRequest;
import com.smartinstitute.erp.report.dto.response.StudentReportPageResponse;


import java.util.List;

public interface StudentReportService {

    StudentReportPageResponse getStudentReport(
            StudentReportRequest request
    );}