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
import com.smartinstitute.erp.test.dto.request.SaveAnswerRequest;
import com.smartinstitute.erp.test.dto.request.StartTestRequest;
import com.smartinstitute.erp.test.dto.response.*;
import com.smartinstitute.erp.test.entity.*;
import com.smartinstitute.erp.test.mapper.StudentQuestionMapper;
import com.smartinstitute.erp.test.mapper.StudentTestMapper;
import com.smartinstitute.erp.test.repository.*;
import com.smartinstitute.erp.test.service.StudentTestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
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

    private final StudentAnswerRepository studentAnswerRepository;

    private final QuestionRepository questionRepository;

    private final QuestionOptionRepository questionOptionRepository;

    public StudentTestServiceImpl(
            SecurityUtil securityUtil,
            InstituteAccessValidator instituteAccessValidator,
            StudentRepository studentRepository,
            TestRepository testRepository,
            StudentTestRepository studentTestRepository,
            StudentTestMapper studentTestMapper, StudentQuestionMapper studentQuestionMapper, TestQuestionRepository testQuestionRepository, StudentAnswerRepository studentAnswerRepository, QuestionRepository questionRepository, QuestionOptionRepository questionOptionRepository) {

        super(securityUtil, instituteAccessValidator);

        this.studentRepository = studentRepository;
        this.testRepository = testRepository;
        this.studentTestRepository = studentTestRepository;
        this.studentTestMapper = studentTestMapper;
        this.studentQuestionMapper = studentQuestionMapper;
        this.testQuestionRepository = testQuestionRepository;
        this.studentAnswerRepository = studentAnswerRepository;
        this.questionRepository = questionRepository;
        this.questionOptionRepository = questionOptionRepository;
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

    @Override
    public StudentAnswerResponse saveAnswer(
            Long studentTestId,
            SaveAnswerRequest request) {

        Student student = getCurrentStudent();

        StudentTest studentTest = studentTestRepository
                .findByIdAndStudent(studentTestId, student)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student test not found."));

        if (studentTest.getStatus() != StudentTestStatus.IN_PROGRESS) {
            throw new BadRequestException(
                    "Test is already submitted.");
        }

        Question question = questionRepository
                .findById(request.getQuestionId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Question not found."));

        StudentAnswer studentAnswer =
                studentAnswerRepository
                        .findByStudentTestAndQuestion(studentTest, question)
                        .orElseGet(() -> {

                            StudentAnswer answer = new StudentAnswer();

                            answer.setStudentTest(studentTest);

                            answer.setQuestion(question);

                            return answer;
                        });

        if (request.getSelectedOptionId() == null) {

            studentAnswer.setSelectedOption(null);

        } else {

            QuestionOption option =
                    questionOptionRepository
                            .findById(request.getSelectedOptionId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Option not found."));

            if (!option.getQuestion().getId().equals(question.getId())) {

                throw new BadRequestException(
                        "Selected option does not belong to question.");
            }

            studentAnswer.setSelectedOption(option);
        }

        studentAnswerRepository.save(studentAnswer);

        updateUnansweredQuestions(studentTest);

        return StudentAnswerResponse.builder()
                .questionId(question.getId())
                .selectedOptionId(
                        studentAnswer.getSelectedOption() != null
                                ? studentAnswer.getSelectedOption().getId()
                                : null
                )
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentAnswerResponse> getSavedAnswers(
            Long studentTestId) {

        Student student = getCurrentStudent();

        StudentTest studentTest =
                studentTestRepository
                        .findByIdAndStudent(studentTestId, student)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Student test not found."));

        return studentAnswerRepository
                .findAllByStudentTest(studentTest)
                .stream()
                .map(answer ->
                        StudentAnswerResponse.builder()
                                .questionId(answer.getQuestion().getId())
                                .selectedOptionId(
                                        answer.getSelectedOption() != null
                                                ? answer.getSelectedOption().getId()
                                                : null
                                )
                                .build())
                .toList();
    }

    private void updateUnansweredQuestions(
            StudentTest studentTest) {

        long answeredQuestions =
                studentAnswerRepository
                        .findAllByStudentTest(studentTest)
                        .stream()
                        .filter(answer ->
                                answer.getSelectedOption() != null)
                        .count();

        studentTest.setUnansweredQuestions(
                studentTest.getTotalQuestions() - (int) answeredQuestions
        );

        studentTestRepository.save(studentTest);
    }

    @Override
    @Transactional
    public void submitTest(Long studentTestId) {

        Student student = getCurrentStudent();

        StudentTest studentTest = studentTestRepository
                .findByIdAndStudent(studentTestId, student)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student test not found."));

        if (studentTest.getStatus() != StudentTestStatus.IN_PROGRESS) {
            throw new BadRequestException(
                    "Test is already submitted.");
        }

        // Evaluation logic will be implemented in Part 3
        evaluateTest(studentTest);

        studentTest.setStatus(StudentTestStatus.SUBMITTED);

        studentTest.setSubmittedAt(LocalDateTime.now());

        long seconds =
                Duration.between(
                        studentTest.getStartedAt(),
                        studentTest.getSubmittedAt()
                ).getSeconds();

        studentTest.setTimeTakenInSeconds((int) seconds);

        studentTestRepository.save(studentTest);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentTestResultResponse getResult(
            Long studentTestId) {

        Student student = getCurrentStudent();

        StudentTest studentTest = studentTestRepository
                .findByIdAndStudent(studentTestId, student)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student test not found."));

        if (studentTest.getStatus() != StudentTestStatus.SUBMITTED) {

            throw new BadRequestException(
                    "Test has not been submitted yet.");
        }

        return StudentTestResultResponse.builder()
                .studentTestId(studentTest.getId())
                .testId(studentTest.getTest().getId())
                .testTitle(studentTest.getTest().getTitle())
                .totalQuestions(studentTest.getTotalQuestions())
                .correctAnswers(studentTest.getCorrectAnswers())
                .wrongAnswers(studentTest.getWrongAnswers())
                .unansweredQuestions(studentTest.getUnansweredQuestions())
                .totalMarks(studentTest.getTotalMarks())
                .obtainedMarks(studentTest.getObtainedMarks())
                .percentage(studentTest.getPercentage())
                .passingPercentage(
                        studentTest.getTest().getPassingPercentage())
                .passed(studentTest.getPassed())
                .startedAt(studentTest.getStartedAt())
                .submittedAt(studentTest.getSubmittedAt())
                .timeTakenInSeconds(
                        studentTest.getTimeTakenInSeconds())
                .build();
    }

    private void evaluateTest(StudentTest studentTest) {

        List<StudentAnswer> answers =
                studentAnswerRepository.findAllByStudentTest(studentTest);

        int correctAnswers = 0;
        int wrongAnswers = 0;
        int unansweredQuestions = 0;
        int obtainedMarks = 0;

        for (StudentAnswer answer : answers) {

            if (answer.getSelectedOption() == null) {

                answer.setCorrect(false);
                answer.setMarksObtained(0);

                unansweredQuestions++;

                continue;
            }

            if (Boolean.TRUE.equals(answer.getSelectedOption().getCorrect())) {

                answer.setCorrect(true);

                int marks = answer.getQuestion().getMarks();

                answer.setMarksObtained(marks);

                obtainedMarks += marks;

                correctAnswers++;

            } else {

                answer.setCorrect(false);

                answer.setMarksObtained(0);

                wrongAnswers++;
            }
        }

        studentAnswerRepository.saveAll(answers);

        int totalQuestions =
                studentTest.getTotalQuestions();

        // Handles questions that never received a StudentAnswer record.
        unansweredQuestions =
                totalQuestions - correctAnswers - wrongAnswers;

        studentTest.setCorrectAnswers(correctAnswers);

        studentTest.setWrongAnswers(wrongAnswers);

        studentTest.setUnansweredQuestions(unansweredQuestions);

        studentTest.setObtainedMarks(obtainedMarks);

        calculatePercentage(studentTest);

        calculatePassFail(studentTest);
    }

    private void calculatePercentage(
            StudentTest studentTest) {

        if (studentTest.getTotalMarks() == 0) {

            studentTest.setPercentage(BigDecimal.ZERO);

            return;
        }

        BigDecimal percentage =
                BigDecimal.valueOf(studentTest.getObtainedMarks())
                        .multiply(BigDecimal.valueOf(100))
                        .divide(
                                BigDecimal.valueOf(studentTest.getTotalMarks()),
                                2,
                                RoundingMode.HALF_UP
                        );

        studentTest.setPercentage(percentage);
    }

    private void calculatePassFail(
            StudentTest studentTest) {

        boolean passed =
                studentTest.getPercentage()
                        .compareTo(
                                BigDecimal.valueOf(
                                        studentTest.getTest()
                                                .getPassingPercentage()
                                )
                        ) >= 0;

        studentTest.setPassed(passed);
    }


}