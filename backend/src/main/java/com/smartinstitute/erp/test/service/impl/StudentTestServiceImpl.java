package com.smartinstitute.erp.test.service.impl;

import com.smartinstitute.erp.common.enums.test.StudentTestStatus;
import com.smartinstitute.erp.common.enums.test.TestStatus;
import com.smartinstitute.erp.common.exception.BadRequestException;
import com.smartinstitute.erp.common.exception.ResourceNotFoundException;
import com.smartinstitute.erp.common.service.BaseCrudService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.security.util.SecurityUtil;
import com.smartinstitute.erp.student.entity.Student;
import com.smartinstitute.erp.student.repository.StudentRepository;
import com.smartinstitute.erp.test.dto.request.StartTestRequest;
import com.smartinstitute.erp.test.dto.response.QuestionForStudentResponse;
import com.smartinstitute.erp.test.dto.response.StudentTestResponse;
import com.smartinstitute.erp.test.dto.response.StudentTestSummaryResponse;
import com.smartinstitute.erp.test.entity.Question;
import com.smartinstitute.erp.test.entity.StudentTest;
import com.smartinstitute.erp.test.entity.Test;
import com.smartinstitute.erp.test.entity.TestQuestion;
import com.smartinstitute.erp.test.mapper.StudentQuestionMapper;
import com.smartinstitute.erp.test.mapper.StudentTestMapper;
import com.smartinstitute.erp.test.repository.StudentTestRepository;
import com.smartinstitute.erp.test.repository.TestQuestionRepository;
import com.smartinstitute.erp.test.repository.TestRepository;
import com.smartinstitute.erp.test.service.StudentTestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class StudentTestServiceImpl extends BaseCrudService
        implements StudentTestService {

    private final StudentRepository studentRepository;

    private final TestRepository testRepository;

    private final StudentTestRepository studentTestRepository;

    private final StudentTestMapper studentTestMapper;

    private final StudentQuestionMapper studentQuestionMapper;

    private final TestQuestionRepository testQuestionRepository;

    public StudentTestServiceImpl(
            SecurityUtil securityUtil,
            InstituteAccessValidator instituteAccessValidator,
            StudentRepository studentRepository,
            TestRepository testRepository,
            StudentTestRepository studentTestRepository,
            StudentTestMapper studentTestMapper, StudentQuestionMapper studentQuestionMapper, TestQuestionRepository testQuestionRepository) {

        super(securityUtil, instituteAccessValidator);

        this.studentRepository = studentRepository;
        this.testRepository = testRepository;
        this.studentTestRepository = studentTestRepository;
        this.studentTestMapper = studentTestMapper;
        this.studentQuestionMapper = studentQuestionMapper;
        this.testQuestionRepository = testQuestionRepository;
    }

    @Override
    public StudentTestResponse startTest(StartTestRequest request) {

        Student student = getCurrentStudent();

        Test test = testRepository.findById(request.getTestId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Test not found."));

        validateStudentCanStartTest(student, test);

        Integer attemptNo = getNextAttemptNumber(student, test);

        StudentTest studentTest = new StudentTest();

        studentTest.setStudent(student);
        studentTest.setTest(test);
        studentTest.setAttemptNo(attemptNo);
        studentTest.setStartedAt(LocalDateTime.now());
        studentTest.setStatus(StudentTestStatus.IN_PROGRESS);

        studentTest.setTotalQuestions(test.getTestQuestions().size());

        studentTest.setTotalMarks(
                test.getTestQuestions()
                        .stream()
                        .mapToInt(tq -> tq.getQuestion().getMarks())
                        .sum()
        );

        studentTest.setUnansweredQuestions(
                studentTest.getTotalQuestions()
        );

        studentTestRepository.save(studentTest);

        return studentTestMapper.toResponse(studentTest);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentTestResponse getStudentTest(Long studentTestId) {

        Student student = getCurrentStudent();

        StudentTest studentTest =
                studentTestRepository
                        .findByIdAndStudent(studentTestId, student)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Student test not found."));

        return studentTestMapper.toResponse(studentTest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentTestSummaryResponse> getMyTests() {

        Student student = getCurrentStudent();

        return studentTestMapper.toSummaryResponseList(
                studentTestRepository
                        .findByStudentOrderByStartedAtDesc(student)
        );
    }

    private void validateStudentCanStartTest(
            Student student,
            Test test) {

        if (!Boolean.TRUE.equals(test.getActive())) {
            throw new BadRequestException(
                    "Test is inactive.");
        }

        if (test.getStatus() != TestStatus.PUBLISHED) {
            throw new BadRequestException(
                    "Test is not published.");
        }

        if (studentTestRepository.existsByStudentAndTestAndStatus(
                student,
                test,
                StudentTestStatus.IN_PROGRESS)) {

            throw new BadRequestException(
                    "You already have an active attempt.");
        }

        long attempts =
                studentTestRepository.countByStudentAndTest(
                        student,
                        test);

        if (attempts >= test.getMaxAttempts()) {

            throw new BadRequestException(
                    "Maximum attempts exceeded.");
        }

        LocalDateTime now = LocalDateTime.now();

        if (test.getStartTime() != null &&
                now.isBefore(test.getStartTime())) {

            throw new BadRequestException(
                    "Test has not started yet.");
        }

        if (test.getEndTime() != null &&
                now.isAfter(test.getEndTime())) {

            throw new BadRequestException(
                    "Test has already ended.");
        }
    }

    private Integer getNextAttemptNumber(
            Student student,
            Test test) {

        return studentTestRepository
                .findTopByStudentAndTestOrderByAttemptNoDesc(
                        student,
                        test)
                .map(studentTest ->
                        studentTest.getAttemptNo() + 1)
                .orElse(1);
    }

    private Student getCurrentStudent() {

        Long studentId = securityUtil.getCurrentUserId();

        return studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionForStudentResponse> getQuestions(Long studentTestId) {

        Student student = getCurrentStudent();

        StudentTest studentTest = studentTestRepository
                .findByIdAndStudent(studentTestId, student)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student test not found."));

        List<TestQuestion> testQuestions =
                testQuestionRepository.findByTestOrderByDisplayOrderAsc(
                        studentTest.getTest()
                );

        List<Question> questions = testQuestions.stream()
                .map(TestQuestion::getQuestion)
                .toList();

        if (Boolean.TRUE.equals(studentTest.getTest().getShuffleQuestions())) {

            questions = new ArrayList<>(questions);

            Collections.shuffle(questions);
        }

        List<QuestionForStudentResponse> responses =
                studentQuestionMapper.toResponseList(questions);

        if (Boolean.TRUE.equals(studentTest.getTest().getShuffleOptions())) {

            responses.forEach(question ->
                    Collections.shuffle(question.getOptions()));
        }

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionForStudentResponse getQuestion(
            Long studentTestId,
            Long questionId) {

        return getQuestions(studentTestId)
                .stream()
                .filter(question ->
                        question.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException("Question not found."));
    }

}