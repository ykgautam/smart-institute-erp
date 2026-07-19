package com.smartinstitute.erp.dashboard.service.impl;

import com.smartinstitute.erp.common.enums.test.StudentTestStatus;
import com.smartinstitute.erp.common.enums.test.TestStatus;
import com.smartinstitute.erp.common.service.StudentBaseService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.dashboard.dto.response.RecentTestResponse;
import com.smartinstitute.erp.dashboard.dto.response.StudentDashboardResponse;
import com.smartinstitute.erp.dashboard.dto.response.UpcomingTestResponse;
import com.smartinstitute.erp.dashboard.service.StudentDashboardService;
import com.smartinstitute.erp.security.util.SecurityUtil;
import com.smartinstitute.erp.student.entity.Student;
import com.smartinstitute.erp.student.repository.StudentRepository;
import com.smartinstitute.erp.test.entity.StudentTest;
import com.smartinstitute.erp.test.entity.Test;
import com.smartinstitute.erp.test.repository.StudentTestRepository;
import com.smartinstitute.erp.test.repository.TestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class StudentDashboardServiceImpl extends StudentBaseService
        implements StudentDashboardService {

    private final StudentTestRepository studentTestRepository;

    private final TestRepository testRepository;

    public StudentDashboardServiceImpl(
            SecurityUtil securityUtil,
            InstituteAccessValidator instituteAccessValidator,
            StudentRepository studentRepository, StudentTestRepository studentTestRepository, TestRepository testRepository) {

        super(
                securityUtil,
                instituteAccessValidator,
                studentRepository
        );
        this.studentTestRepository = studentTestRepository;
        this.testRepository = testRepository;
    }

    @Override
    public StudentDashboardResponse getDashboard() {

        Student student = getCurrentStudent();

        long testsAttempted =
                studentTestRepository.countByStudent(student);

        long publishedTests =
                testRepository.countByStatus(
                        TestStatus.PUBLISHED
                );

        long pendingTests =
                Math.max(
                        0,
                        publishedTests - testsAttempted
                );

        Double averageScore =
                studentTestRepository
                        .getAveragePercentage(student);

        long totalPassed =
                studentTestRepository
                        .countByStudentAndPassedTrue(student);

        long totalFailed =
                studentTestRepository
                        .countByStudentAndPassedFalse(student);

        Double highestScore =
                studentTestRepository.getHighestPercentage(student);

        Double lowestScore =
                studentTestRepository.getLowestPercentage(student);

        Double passPercentage =
                testsAttempted == 0
                        ? 0.0
                        : (totalPassed * 100.0) / testsAttempted;

        List<RecentTestResponse> recentTests =
                studentTestRepository
                        .findTop5ByStudentAndStatusOrderBySubmittedAtDesc(
                                student,
                                StudentTestStatus.SUBMITTED
                        )
                        .stream()
                        .map(this::toRecentTestResponse)
                        .toList();

        List<UpcomingTestResponse> upcomingTests =
                testRepository
                        .findTop5ByStatusAndStartTimeAfterOrderByStartTimeAsc(
                                TestStatus.PUBLISHED,
                                LocalDateTime.now()
                        )
                        .stream()
                        .map(this::toUpcomingTestResponse)
                        .toList();

        return StudentDashboardResponse.builder()

                .studentId(student.getId())

                .studentName(
                        student.getFirstName()
                                + " "
                                + student.getLastName()
                )

                .testsAttempted((int) testsAttempted)

                .pendingTests((int) pendingTests)

                .averageScore(averageScore)

                .totalPassed((int) totalPassed)

                .totalFailed((int) totalFailed)

                .highestScore(highestScore)

                .lowestScore(lowestScore)

                .passPercentage(passPercentage)

                .recentTests(recentTests)

                .upcomingTests(upcomingTests)

                .build();
    }

    private RecentTestResponse toRecentTestResponse(
            StudentTest studentTest) {

        return RecentTestResponse.builder()
                .studentTestId(studentTest.getId())
                .testId(studentTest.getTest().getId())
                .testTitle(studentTest.getTest().getTitle())
                .percentage(studentTest.getPercentage())
                .passed(studentTest.getPassed())
                .submittedAt(studentTest.getSubmittedAt())
                .build();
    }

    private UpcomingTestResponse toUpcomingTestResponse(
            Test test) {

        return UpcomingTestResponse.builder()
                .testId(test.getId())
                .testTitle(test.getTitle())
                .courseName(test.getCourse().getCourseName())
                .durationInMinutes(test.getDurationMinutes())
                .startTime(test.getStartTime())
                .endTime(test.getEndTime())
                .build();
    }


}