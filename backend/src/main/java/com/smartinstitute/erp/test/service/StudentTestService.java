package com.smartinstitute.erp.test.service;

import com.smartinstitute.erp.test.dto.request.StartTestRequest;
import com.smartinstitute.erp.test.dto.response.QuestionForStudentResponse;
import com.smartinstitute.erp.test.dto.response.StudentTestResponse;
import com.smartinstitute.erp.test.dto.response.StudentTestSummaryResponse;

import java.util.List;

public interface StudentTestService {

    StudentTestResponse startTest(StartTestRequest request);

    StudentTestResponse getStudentTest(Long studentTestId);

    List<StudentTestSummaryResponse> getMyTests();

    List<QuestionForStudentResponse> getQuestions(Long studentTestId);

    QuestionForStudentResponse getQuestion(
            Long studentTestId,
            Long questionId
    );

}