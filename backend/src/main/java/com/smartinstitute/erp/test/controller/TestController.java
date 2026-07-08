package com.smartinstitute.erp.test.controller;

import com.smartinstitute.erp.test.dto.request.CreateTestRequest;
import com.smartinstitute.erp.test.dto.request.UpdateTestRequest;
import com.smartinstitute.erp.test.dto.response.TestResponse;
import com.smartinstitute.erp.test.service.TestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TestResponse createTest(
            @Valid @RequestBody CreateTestRequest request
    ) {

        return testService.createTest(request);
    }

    @PutMapping("/{testId}")
    public TestResponse updateTest(
            @PathVariable Long testId,
            @Valid @RequestBody UpdateTestRequest request
    ) {

        return testService.updateTest(
                testId,
                request
        );
    }

    @PutMapping("/{testId}/publish")
    public TestResponse publishTest(
            @PathVariable Long testId
    ) {

        return testService.publishTest(testId);
    }

    @PutMapping("/{testId}/draft")
    public TestResponse draftTest(
            @PathVariable Long testId
    ) {

        return testService.draftTest(testId);
    }

    @DeleteMapping("/{testId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTest(@PathVariable Long testId) {

        testService.deleteTest(testId);
    }

    @GetMapping("/{testId}")
    public TestResponse getTestById(@PathVariable Long testId) {

        return testService.getTestById(testId);
    }

    @GetMapping
    public List<TestResponse> getAllTests() {

        return testService.getAllTests();
    }

    @GetMapping("/course/{courseId}")
    public List<TestResponse> getTestsByCourse(@PathVariable Long courseId) {

        return testService.getTestsByCourse(courseId);
    }

    @GetMapping("/topic/{topicId}")
    public List<TestResponse> getTestsByTopic(@PathVariable Long topicId) {

        return testService.getTestsByTopic(topicId);
    }

    @GetMapping("/course/{courseId}/topic/{topicId}")
    public List<TestResponse> getTestsByCourseAndTopic(
            @PathVariable Long courseId,
            @PathVariable Long topicId
    ) {

        return testService.getTestsByCourseAndTopic(
                courseId,
                topicId
        );
    }
}