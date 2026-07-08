package com.smartinstitute.erp.test.service.impl;

import com.smartinstitute.erp.common.enums.test.TestStatus;
import com.smartinstitute.erp.common.service.BaseCrudService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.course.entity.Course;
import com.smartinstitute.erp.course.repository.CourseRepository;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.security.util.SecurityUtil;
import com.smartinstitute.erp.test.dto.request.CreateTestRequest;
import com.smartinstitute.erp.test.dto.request.TestQuestionRequest;
import com.smartinstitute.erp.test.dto.request.UpdateTestRequest;
import com.smartinstitute.erp.test.dto.response.TestResponse;
import com.smartinstitute.erp.test.entity.Question;
import com.smartinstitute.erp.test.entity.Test;
import com.smartinstitute.erp.test.entity.TestQuestion;
import com.smartinstitute.erp.test.entity.Topic;
import com.smartinstitute.erp.test.mapper.TestMapper;
import com.smartinstitute.erp.test.repository.QuestionRepository;
import com.smartinstitute.erp.test.repository.TestRepository;
import com.smartinstitute.erp.test.repository.TopicRepository;
import com.smartinstitute.erp.test.service.TestService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class TestServiceImpl extends BaseCrudService implements TestService {

    private final TestRepository testRepository;

    private final CourseRepository courseRepository;

    private final TopicRepository topicRepository;

    private final QuestionRepository questionRepository;

    private final TestMapper testMapper;

    public TestServiceImpl(
            SecurityUtil securityUtil,
            InstituteAccessValidator instituteAccessValidator,
            TestRepository testRepository,
            CourseRepository courseRepository,
            TopicRepository topicRepository,
            QuestionRepository questionRepository,
            TestMapper testMapper
    ) {
        super(
                securityUtil,
                instituteAccessValidator
        );

        this.testRepository = testRepository;
        this.courseRepository = courseRepository;
        this.topicRepository = topicRepository;
        this.questionRepository = questionRepository;
        this.testMapper = testMapper;
    }

    // ============================================================
    // Helper Methods
    // ============================================================

//    private Institute getCurrentInstitute() {
//        return getCurrentUserInstitute();
//    }

    private Test getTestOrThrow(Long testId) {

        return testRepository
                .findByIdAndInstituteAndActiveTrue(
                        testId,
                        getCurrentInstitute()
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Test not found."
                        ));
    }

    private Course getCourseOrThrow(Long courseId) {

        return courseRepository
                .findByIdAndInstituteAndActiveTrue(
                        courseId,
                        getCurrentInstitute()
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Course not found."
                        ));
    }

    private Topic getTopicOrThrow(Long topicId) {

        return topicRepository
                .findByIdAndInstituteAndActiveTrue(
                        topicId,
                        getCurrentInstitute()
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Topic not found."
                        ));
    }

    private Question getQuestionOrThrow(Long questionId) {

        return questionRepository
                .findByIdAndInstituteAndActiveTrue(
                        questionId,
                        getCurrentInstitute()
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Question not found."
                        ));
    }

    private void validateSchedule(
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {

        if (startTime != null &&
                endTime != null &&
                !startTime.isBefore(endTime)) {

            throw new IllegalArgumentException(
                    "Start time must be before end time."
            );
        }
    }

    private void validateQuestionBelongsToCourseAndTopic(
            Question question,
            Course course,
            Topic topic
    ) {

        if (!question.getCourse().getId().equals(course.getId())) {

            throw new IllegalArgumentException(
                    "Question does not belong to selected course."
            );
        }

        if (!question.getTopic().getId().equals(topic.getId())) {

            throw new IllegalArgumentException(
                    "Question does not belong to selected topic."
            );
        }
    }

    private void validateDuplicateQuestions(
            List<TestQuestionRequest> requests
    ) {

        Set<Long> questionIds = new HashSet<>();

        Set<Integer> displayOrders = new HashSet<>();

        for (TestQuestionRequest request : requests) {

            if (!questionIds.add(request.getQuestionId())) {

                throw new IllegalArgumentException(
                        "Duplicate question found."
                );
            }

            if (!displayOrders.add(request.getDisplayOrder())) {

                throw new IllegalArgumentException(
                        "Duplicate display order found."
                );
            }
        }
    }

    private TestResponse buildTestResponse(Test test) {

        TestResponse response = testMapper.toResponse(test);

        response.setQuestionCount(test.getTestQuestions().size());

        return response;
    }

    // createTest() -> Part 2
    @Override
    public TestResponse createTest(CreateTestRequest request) {

        Institute institute = getCurrentInstitute();

        Course course = getCourseOrThrow(
                request.getCourseId()
        );

        Topic topic = getTopicOrThrow(
                request.getTopicId()
        );

        if (!topic.getCourse().getId().equals(course.getId())) {
            throw new IllegalArgumentException(
                    "Selected topic does not belong to selected course."
            );
        }

        validateSchedule(
                request.getStartTime(),
                request.getEndTime()
        );

        validateDuplicateQuestions(
                request.getQuestions()
        );

        if (testRepository.existsByInstituteAndCourseAndTopicAndTitleAndActiveTrue(
                institute,
                course,
                topic,
                request.getTitle())) {

            throw new IllegalArgumentException(
                    "Test with same title already exists."
            );
        }

        Test test = new Test();

        test.setInstitute(institute);

        test.setCourse(course);

        test.setTopic(topic);

        test.setTitle(request.getTitle());

        test.setDescription(request.getDescription());

        test.setTestType(request.getTestType());

        test.setPassingPercentage(request.getPassingPercentage());

        test.setShuffleQuestions(request.getShuffleQuestions());

        test.setShuffleOptions(request.getShuffleOptions());

        test.setTimerEnabled(request.getTimerEnabled());

        test.setDurationMinutes(request.getDurationMinutes());

        test.setShowExplanationAfterSubmission(request.getShowExplanationAfterSubmission());

        test.setMaxAttempts(request.getMaxAttempts());

        test.setStartTime(request.getStartTime());

        test.setEndTime(request.getEndTime());

        test.setStatus(TestStatus.DRAFT);

        test.setActive(true);

        if (request.getQuestions().isEmpty()) {
            throw new IllegalArgumentException(
                    "Test must contain at least one question."
            );
        }

        for (TestQuestionRequest questionRequest : request.getQuestions()) {

            Question question = getQuestionOrThrow(
                    questionRequest.getQuestionId()
            );

            validateQuestionBelongsToCourseAndTopic(
                    question,
                    course,
                    topic
            );

            TestQuestion testQuestion = new TestQuestion();

            testQuestion.setQuestion(question);

            testQuestion.setDisplayOrder(
                    questionRequest.getDisplayOrder()
            );

            testQuestion.setActive(true);

            test.addTestQuestion(testQuestion);
        }

        Test savedTest = testRepository.save(test);

        return buildTestResponse(savedTest);
    }

    // updateTest() -> Part 3
    @Override
    public TestResponse updateTest(
            Long testId,
            UpdateTestRequest request
    ) {

        Test test = getTestOrThrow(testId);

        if (test.getStatus() == TestStatus.PUBLISHED) {
            throw new IllegalArgumentException(
                    "Published test cannot be modified. Move it to Draft before editing."
            );
        }

        Institute institute = getCurrentInstitute();

        Course course = getCourseOrThrow(
                request.getCourseId()
        );

        Topic topic = getTopicOrThrow(
                request.getTopicId()
        );

        if (!topic.getCourse().getId().equals(course.getId())) {
            throw new IllegalArgumentException(
                    "Selected topic does not belong to selected course."
            );
        }

        validateSchedule(
                request.getStartTime(),
                request.getEndTime()
        );

        validateDuplicateQuestions(
                request.getQuestions()
        );

        if (testRepository.existsByInstituteAndCourseAndTopicAndTitleAndIdNotAndActiveTrue(
                institute,
                course,
                topic,
                request.getTitle(),
                testId
        )) {

            throw new IllegalArgumentException(
                    "Test with same title already exists."
            );
        }

        test.setCourse(course);

        test.setTopic(topic);

        test.setTitle(request.getTitle());

        test.setDescription(request.getDescription());

        test.setTestType(request.getTestType());

        test.setPassingPercentage(request.getPassingPercentage());

        test.setShuffleQuestions(request.getShuffleQuestions());

        test.setShuffleOptions(request.getShuffleOptions());

        test.setTimerEnabled(request.getTimerEnabled());

        test.setDurationMinutes(request.getDurationMinutes());

        test.setShowExplanationAfterSubmission(
                request.getShowExplanationAfterSubmission());

        test.setMaxAttempts(request.getMaxAttempts());

        test.setStartTime(request.getStartTime());

        test.setEndTime(request.getEndTime());

        /*
         * Remove all previously assigned questions.
         */
        test.getTestQuestions().clear();

        /*
         * Add updated questions.
         */
        for (TestQuestionRequest questionRequest : request.getQuestions()) {

            Question question = getQuestionOrThrow(
                    questionRequest.getQuestionId()
            );

            validateQuestionBelongsToCourseAndTopic(
                    question,
                    course,
                    topic
            );

            TestQuestion testQuestion = new TestQuestion();

            testQuestion.setQuestion(question);

            testQuestion.setDisplayOrder(
                    questionRequest.getDisplayOrder()
            );

            testQuestion.setActive(true);

            test.addTestQuestion(testQuestion);
        }

        Test updatedTest = testRepository.save(test);

        return buildTestResponse(updatedTest);
    }

    // publishTest() -> Part 4
    @Override
    public TestResponse publishTest(Long testId) {

        Test test = getTestOrThrow(testId);

        if (test.getStatus() == TestStatus.PUBLISHED) {
            throw new IllegalArgumentException(
                    "Test is already published."
            );
        }

        if (test.getTestQuestions().isEmpty()) {
            throw new IllegalArgumentException(
                    "Cannot publish a test without questions."
            );
        }

        test.setStatus(TestStatus.PUBLISHED);

        Test updatedTest = testRepository.save(test);

        return buildTestResponse(updatedTest);
    }

    @Override
    public TestResponse draftTest(Long testId) {

        Test test = getTestOrThrow(testId);

        if (test.getStatus() == TestStatus.DRAFT) {
            throw new IllegalArgumentException(
                    "Test is already in draft status."
            );
        }

        test.setStatus(TestStatus.DRAFT);

        Test updatedTest = testRepository.save(test);

        return buildTestResponse(updatedTest);
    }

    @Override
    public void deleteTest(Long testId) {

        Test test = getTestOrThrow(testId);

        test.setActive(false);

        testRepository.save(test);
    }

    private void validateDraftStatus(Test test) {

        if (test.getStatus() != TestStatus.DRAFT) {
            throw new IllegalArgumentException(
                    "Only draft tests can be modified."
            );
        }
    }

    // get APIs -> Part 5

    @Override
    @Transactional
    public TestResponse getTestById(Long testId) {

        Test test = getTestOrThrow(testId);

        return buildTestResponse(test);
    }

    @Override
    @Transactional
    public List<TestResponse> getAllTests() {

        return testRepository
                .findByInstituteAndActiveTrue(
                        getCurrentInstitute()
                )
                .stream()
                .map(this::buildTestResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<TestResponse> getTestsByCourse(Long courseId) {

        Course course = getCourseOrThrow(courseId);

        return testRepository
                .findByCourseAndInstituteAndActiveTrue(
                        course,
                        getCurrentInstitute()
                )
                .stream()
                .map(this::buildTestResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<TestResponse> getTestsByTopic(Long topicId) {

        Topic topic = getTopicOrThrow(topicId);

        return testRepository
                .findByTopicAndInstituteAndActiveTrue(
                        topic,
                        getCurrentInstitute()
                )
                .stream()
                .map(this::buildTestResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<TestResponse> getTestsByCourseAndTopic(Long courseId, Long topicId) {

        Course course = getCourseOrThrow(courseId);

        Topic topic = getTopicOrThrow(topicId);

        if (!topic.getCourse().getId().equals(course.getId())) {
            throw new IllegalArgumentException(
                    "Selected topic does not belong to selected course."
            );
        }

        return testRepository
                .findByCourseAndTopicAndInstituteAndActiveTrue(
                        course,
                        topic,
                        getCurrentInstitute()
                )
                .stream()
                .map(this::buildTestResponse)
                .toList();
    }

    private void validateCourseAndTopic(Course course, Topic topic) {

        if (!topic.getCourse().getId().equals(course.getId())) {
            throw new IllegalArgumentException(
                    "Selected topic does not belong to selected course."
            );
        }
    }

}