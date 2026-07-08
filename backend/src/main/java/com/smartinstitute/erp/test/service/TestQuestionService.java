package com.smartinstitute.erp.test.service;

import com.smartinstitute.erp.test.dto.request.AddQuestionToTestRequest;
import com.smartinstitute.erp.test.dto.request.UpdateTestQuestionOrderRequest;
import com.smartinstitute.erp.test.dto.response.TestQuestionResponse;

import java.util.List;

public interface TestQuestionService {

    void addQuestionsToTest(
            Long testId,
            AddQuestionToTestRequest request
    );

    List<TestQuestionResponse> getQuestionsByTest(
            Long testId
    );

    void removeQuestionFromTest(
            Long testId,
            Long questionId
    );

    void updateQuestionOrder(
            Long testId,
            List<UpdateTestQuestionOrderRequest> request
    );

}