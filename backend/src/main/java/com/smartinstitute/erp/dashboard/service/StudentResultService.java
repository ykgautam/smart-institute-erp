package com.smartinstitute.erp.dashboard.service;

import com.smartinstitute.erp.dashboard.dto.response.StudentTestResultResponse;

public interface StudentResultService {

    StudentTestResultResponse getResult(
            Long studentTestId
    );

}