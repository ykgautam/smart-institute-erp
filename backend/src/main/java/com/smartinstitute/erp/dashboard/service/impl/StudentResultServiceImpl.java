package com.smartinstitute.erp.dashboard.service.impl;

import com.smartinstitute.erp.common.enums.test.StudentTestStatus;
import com.smartinstitute.erp.common.exception.BusinessException;
import com.smartinstitute.erp.common.exception.ResourceNotFoundException;
import com.smartinstitute.erp.common.service.StudentBaseService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.dashboard.dto.response.QuestionResultResponse;
import com.smartinstitute.erp.dashboard.dto.response.StudentTestResultResponse;
import com.smartinstitute.erp.dashboard.service.StudentResultService;
import com.smartinstitute.erp.security.util.SecurityUtil;
import com.smartinstitute.erp.student.entity.Student;
import com.smartinstitute.erp.student.repository.StudentRepository;
import com.smartinstitute.erp.test.entity.QuestionOption;
import com.smartinstitute.erp.test.entity.StudentAnswer;
import com.smartinstitute.erp.test.entity.StudentTest;
import com.smartinstitute.erp.test.repository.StudentAnswerRepository;
import com.smartinstitute.erp.test.repository.StudentTestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class StudentResultServiceImpl
        extends StudentBaseService
        implements StudentResultService {

    private final StudentTestRepository studentTestRepository;

    private final StudentAnswerRepository studentAnswerRepository;

    public StudentResultServiceImpl(
            SecurityUtil securityUtil,
            InstituteAccessValidator instituteAccessValidator,
            StudentRepository studentRepository,
            StudentTestRepository studentTestRepository,
            StudentAnswerRepository studentAnswerRepository) {

        super(
                securityUtil,
                instituteAccessValidator,
                studentRepository
        );

        this.studentTestRepository = studentTestRepository;
        this.studentAnswerRepository = studentAnswerRepository;
    }

    @Override
    public StudentTestResultResponse getResult(
            Long studentTestId) {

        Student student = getCurrentStudent();

        StudentTest studentTest =
                studentTestRepository
                        .findByIdAndStudent(
                                studentTestId,
                                student
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Student test not found."
                                ));
        if (studentTest.getStatus() != StudentTestStatus.SUBMITTED) {
            throw new BusinessException(
                    "Result is not available until the test is submitted."
            );
        }

        List<StudentAnswer> studentAnswers =
                studentAnswerRepository
                        .findByStudentTestOrderByQuestion_Id(
                                studentTest
                        );

        if (studentAnswers.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No answers found for this test attempt."
            );
        }

        List<QuestionResultResponse> questions =
                studentAnswers.stream()
                        .map(this::toQuestionResultResponse)
                        .toList();

        return buildResultResponse(studentTest, questions);
    }

    private StudentTestResultResponse buildResultResponse(
            StudentTest studentTest,
            List<QuestionResultResponse> questions) {

        return StudentTestResultResponse.builder()
                .studentTestId(studentTest.getId())
                .testId(studentTest.getTest().getId())
                .testTitle(studentTest.getTest().getTitle())
                .totalQuestions(studentTest.getTotalQuestions())
                .correctAnswers(studentTest.getCorrectAnswers())
                .wrongAnswers(studentTest.getWrongAnswers())
                .unansweredQuestions(studentTest.getUnansweredQuestions())
                .obtainedMarks(studentTest.getObtainedMarks())
                .totalMarks(studentTest.getTotalMarks())
                .percentage(studentTest.getPercentage())
                .passed(studentTest.getPassed())
                .timeTakenInSeconds(studentTest.getTimeTakenInSeconds())
                .submittedAt(studentTest.getSubmittedAt())
                .questions(questions)
                .build();
    }

    private QuestionResultResponse toQuestionResultResponse(
            StudentAnswer studentAnswer) {

        String selectedOption = null;

        if (studentAnswer.getSelectedOption() != null) {
            selectedOption =
                    studentAnswer.getSelectedOption().getOptionText();
        }

        String correctOption =
                studentAnswer.getQuestion()
                        .getOptions()
                        .stream()
                        .filter(QuestionOption::getCorrect)
                        .findFirst()
                        .map(QuestionOption::getOptionText)
                        .orElse(null);

        return QuestionResultResponse.builder()
                .questionId(studentAnswer.getQuestion().getId())
                .question(studentAnswer.getQuestion().getQuestionText())
                .selectedOption(selectedOption)
                .correctOption(correctOption)
                .correct(studentAnswer.getCorrect())
                .marksObtained(studentAnswer.getMarksObtained())
                .build();
    }
}