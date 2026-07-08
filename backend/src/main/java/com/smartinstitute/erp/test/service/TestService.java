package com.smartinstitute.erp.test.service;

import com.smartinstitute.erp.test.dto.request.CreateTestRequest;
import com.smartinstitute.erp.test.dto.request.UpdateTestRequest;
import com.smartinstitute.erp.test.dto.response.TestResponse;

import java.util.List;

public interface TestService {

    TestResponse createTest(CreateTestRequest request);

    TestResponse updateTest(Long testId, UpdateTestRequest request);

    TestResponse publishTest(Long testId);

    TestResponse draftTest(Long testId);

    void deleteTest(Long testId);

    TestResponse getTestById(Long testId);

    List<TestResponse> getAllTests();

    List<TestResponse> getTestsByCourse(Long courseId);

    List<TestResponse> getTestsByTopic(Long topicId);

    List<TestResponse> getTestsByCourseAndTopic(Long courseId, Long topicId);

}