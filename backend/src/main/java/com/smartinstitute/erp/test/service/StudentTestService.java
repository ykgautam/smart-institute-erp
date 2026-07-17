package com.smartinstitute.erp.test.service;

import com.smartinstitute.erp.test.dto.request.SaveAnswerRequest;
import com.smartinstitute.erp.test.dto.request.StartTestRequest;
import com.smartinstitute.erp.test.dto.response.*;

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

    StudentAnswerResponse saveAnswer(Long studentTestId, SaveAnswerRequest request);

    List<StudentAnswerResponse> getSavedAnswers(Long studentTestId);

    void submitTest(Long studentTestId);

    StudentTestResultResponse getResult(Long studentTestId);
}