package com.smartinstitute.erp.test.service.impl;

import com.smartinstitute.erp.common.exception.ResourceNotFoundException;
import com.smartinstitute.erp.common.service.BaseCrudService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.security.util.SecurityUtil;
import com.smartinstitute.erp.test.dto.request.AddQuestionToTestRequest;
import com.smartinstitute.erp.test.dto.request.UpdateTestQuestionOrderRequest;
import com.smartinstitute.erp.test.dto.response.TestQuestionResponse;
import com.smartinstitute.erp.test.entity.Question;
import com.smartinstitute.erp.test.entity.Test;
import com.smartinstitute.erp.test.entity.TestQuestion;
import com.smartinstitute.erp.test.mapper.TestQuestionMapper;
import com.smartinstitute.erp.test.repository.QuestionRepository;
import com.smartinstitute.erp.test.repository.TestQuestionRepository;
import com.smartinstitute.erp.test.repository.TestRepository;
import com.smartinstitute.erp.test.service.TestQuestionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class TestQuestionServiceImpl extends BaseCrudService implements TestQuestionService {

    private final TestRepository testRepository;

    private final QuestionRepository questionRepository;

    private final TestQuestionRepository testQuestionRepository;

    private final TestQuestionMapper testQuestionMapper;

    public TestQuestionServiceImpl(SecurityUtil securityUtil, InstituteAccessValidator instituteAccessValidator, TestRepository testRepository, QuestionRepository questionRepository, TestQuestionRepository testQuestionRepository, TestQuestionMapper testQuestionMapper) {
        super(securityUtil, instituteAccessValidator);
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
        this.testQuestionRepository = testQuestionRepository;
        this.testQuestionMapper = testQuestionMapper;
    }

    @Override
    public void addQuestionsToTest(
            Long testId,
            AddQuestionToTestRequest request) {

        Test test = getTest(testId);

        int displayOrder = test.getTestQuestions().size() + 1;

        for (Long questionId : request.getQuestionIds()) {

            Question question = getQuestion(questionId);

            validateQuestionBelongsToTestCourse(test, question);

            if (testQuestionRepository.existsByTestAndQuestion(test, question)) {
                continue;
            }

            TestQuestion testQuestion = new TestQuestion();

            testQuestion.setTest(test);
            testQuestion.setQuestion(question);
            testQuestion.setDisplayOrder(displayOrder++);
            testQuestion.setActive(true);

            test.getTestQuestions().add(testQuestion);

        }

        testRepository.save(test);

    }

    @Override
    @Transactional(readOnly = true)
    public List<TestQuestionResponse> getQuestionsByTest(Long testId) {

        Test test = getTest(testId);

        return testQuestionRepository
                .findByTestOrderByDisplayOrderAsc(test)
                .stream()
                .map(testQuestionMapper::toResponse)
                .toList();

    }

    @Override
    public void removeQuestionFromTest(Long testId, Long questionId) {

        Test test = getTest(testId);

        Question question = getQuestion(questionId);

        TestQuestion testQuestion = testQuestionRepository
                .findByTestAndQuestion(
                        test,
                        question
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Question is not assigned to this test."
                        )
                );

        test.removeTestQuestion(testQuestion);

        testRepository.save(test);

        normalizeDisplayOrder(test);

    }

    @Override
    public void updateQuestionOrder(
            Long testId,
            List<UpdateTestQuestionOrderRequest> request
    ) {

        Test test = getTest(testId);

        List<TestQuestion> testQuestions =
                testQuestionRepository.findByTestOrderByDisplayOrderAsc(test);

        validateQuestionOrderRequest(
                request,
                testQuestions
        );

        Map<Long, Integer> displayOrderMap = request.stream()
                .collect(Collectors.toMap(
                        UpdateTestQuestionOrderRequest::getQuestionId,
                        UpdateTestQuestionOrderRequest::getDisplayOrder
                ));

        for (TestQuestion testQuestion : testQuestions) {

            Integer displayOrder =
                    displayOrderMap.get(testQuestion.getQuestion().getId());

            if (displayOrder != null) {

                testQuestion.setDisplayOrder(displayOrder);

            }

        }

        testQuestionRepository.saveAll(testQuestions);

    }

    private void validateQuestionOrderRequest(
            List<UpdateTestQuestionOrderRequest> request,
            List<TestQuestion> testQuestions
    ) {

        if (request.size() != testQuestions.size()) {

            throw new IllegalArgumentException(
                    "Question order list size does not match assigned questions."
            );

        }

        Set<Long> assignedQuestionIds = testQuestions.stream()
                .map(testQuestion ->
                        testQuestion.getQuestion().getId())
                .collect(Collectors.toSet());

        Set<Long> requestQuestionIds = request.stream()
                .map(UpdateTestQuestionOrderRequest::getQuestionId)
                .collect(Collectors.toSet());

        if (!assignedQuestionIds.equals(requestQuestionIds)) {

            throw new IllegalArgumentException(
                    "Question list does not match assigned questions."
            );

        }

        long uniqueOrders = request.stream()
                .map(UpdateTestQuestionOrderRequest::getDisplayOrder)
                .distinct()
                .count();

        if (uniqueOrders != request.size()) {

            throw new IllegalArgumentException(
                    "Display order must be unique."
            );

        }

    }

    private void normalizeDisplayOrder(Test test) {

        List<TestQuestion> questions =
                testQuestionRepository.findByTestOrderByDisplayOrderAsc(test);

        int order = 1;

        for (TestQuestion question : questions) {

            question.setDisplayOrder(order++);

        }

        testQuestionRepository.saveAll(questions);

    }

    private Test getTest(Long testId) {

        Institute institute = securityUtil.getCurrentInstitute();

        return testRepository
                .findByIdAndInstituteAndActiveTrue(
                        testId,
                        institute
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Test not found with id : " + testId
                        )
                );

    }

    private Question getQuestion(Long questionId) {

        Institute institute = securityUtil.getCurrentInstitute();

        return questionRepository
                .findByIdAndInstituteAndActiveTrue(
                        questionId,
                        institute
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Question not found with id : " + questionId
                        )
                );

    }

    private void validateQuestionBelongsToTestCourse(
            Test test,
            Question question
    ) {

        if (!test.getCourse().getId().equals(question.getCourse().getId())) {

            throw new IllegalArgumentException(
                    "Question does not belong to the Test Course."
            );

        }

    }

}