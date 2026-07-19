package com.smartinstitute.erp.dashboard.service.impl;

import com.smartinstitute.erp.common.service.StudentBaseService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.dashboard.dto.response.CoursePerformanceResponse;
import com.smartinstitute.erp.dashboard.dto.response.MonthlyPerformanceResponse;
import com.smartinstitute.erp.dashboard.dto.response.StudentPerformanceResponse;
import com.smartinstitute.erp.dashboard.dto.response.TopicPerformanceResponse;
import com.smartinstitute.erp.dashboard.projection.CoursePerformanceProjection;
import com.smartinstitute.erp.dashboard.projection.MonthlyPerformanceProjection;
import com.smartinstitute.erp.dashboard.projection.TopicPerformanceProjection;
import com.smartinstitute.erp.dashboard.service.StudentPerformanceService;
import com.smartinstitute.erp.security.util.SecurityUtil;
import com.smartinstitute.erp.student.entity.Student;
import com.smartinstitute.erp.student.repository.StudentRepository;
import com.smartinstitute.erp.test.repository.StudentTestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class StudentPerformanceServiceImpl
        extends StudentBaseService
        implements StudentPerformanceService {

    private final StudentTestRepository
            studentTestRepository;

    public StudentPerformanceServiceImpl(

            SecurityUtil securityUtil,

            InstituteAccessValidator instituteAccessValidator,

            StudentRepository studentRepository,

            StudentTestRepository studentTestRepository) {

        super(
                securityUtil,
                instituteAccessValidator,
                studentRepository
        );

        this.studentTestRepository = studentTestRepository;
    }

    @Override
    public StudentPerformanceResponse getPerformance() {

        Student student = getCurrentStudent();

        int testsAttempted =
                (int) studentTestRepository.countByStudent(student);

        int testsPassed =
                (int) studentTestRepository
                        .countByStudentAndPassedTrue(student);

        int testsFailed =
                (int) studentTestRepository
                        .countByStudentAndPassedFalse(student);

        double overallAverage =
                Optional.ofNullable(
                        studentTestRepository
                                .getAveragePercentage(student)
                ).orElse(0.0);

        double highestScore =
                Optional.ofNullable(
                        studentTestRepository
                                .getHighestPercentage(student)
                ).orElse(0.0);

        double lowestScore =
                Optional.ofNullable(
                        studentTestRepository
                                .getLowestPercentage(student)
                ).orElse(0.0);

        double passPercentage = testsAttempted == 0
                ? 0
                : (testsPassed * 100.0) / testsAttempted;

        return StudentPerformanceResponse.builder()

                .overallAverage(overallAverage)

                .highestScore(highestScore)

                .lowestScore(lowestScore)

                .testsAttempted(testsAttempted)

                .testsPassed(testsPassed)

                .testsFailed(testsFailed)

                .passPercentage(passPercentage)

                .monthlyPerformance(getMonthlyPerformance(student))

                .coursePerformance(getCoursePerformance(student))

                .strongTopics(getStrongTopics(student))

                .weakTopics(getWeakTopics(student))

                .build();
    }

    private MonthlyPerformanceResponse toMonthlyPerformanceResponse(
            MonthlyPerformanceProjection projection) {

        return MonthlyPerformanceResponse.builder()

                .year(projection.getYear())

                .month(projection.getMonth())

                .averagePercentage(projection.getAveragePercentage())

                .testsAttempted(projection.getTestsAttempted().intValue())

                .build();
    }

    private CoursePerformanceResponse toCoursePerformanceResponse(
            CoursePerformanceProjection projection) {

        double passPercentage = 0;

        if (projection.getTestsAttempted() > 0) {

            passPercentage =
                    (projection.getTestsPassed() * 100.0)
                            / projection.getTestsAttempted();
        }

        return CoursePerformanceResponse.builder()

                .courseId(projection.getCourseId())

                .courseName(projection.getCourseName())

                .testsAttempted(projection.getTestsAttempted().intValue())

                .testsPassed(projection.getTestsPassed().intValue())

                .averagePercentage(projection.getAveragePercentage())

                .passPercentage(passPercentage)

                .build();
    }

    private TopicPerformanceResponse toTopicPerformanceResponse(
            TopicPerformanceProjection projection) {

        return TopicPerformanceResponse.builder()

                .topicId(projection.getTopicId())

                .topicName(projection.getTopicName())

                .averagePercentage(projection.getAveragePercentage())

                .testsAttempted(projection.getTestsAttempted().intValue())

                .build();
    }

    private List<MonthlyPerformanceResponse> getMonthlyPerformance(
            Student student) {

        return studentTestRepository
                .getMonthlyPerformance(student)
                .stream()
                .map(this::toMonthlyPerformanceResponse)
                .toList();
    }

    private List<CoursePerformanceResponse> getCoursePerformance(
            Student student) {

        return studentTestRepository
                .getCoursePerformance(student)
                .stream()
                .map(this::toCoursePerformanceResponse)
                .toList();
    }

    private List<TopicPerformanceResponse> getStrongTopics(
            Student student) {

        return studentTestRepository
                .getStrongTopics(student)
                .stream()
                .map(this::toTopicPerformanceResponse)
                .toList();
    }

    private List<TopicPerformanceResponse> getWeakTopics(
            Student student) {

        return studentTestRepository
                .getWeakTopics(student)
                .stream()
                .map(this::toTopicPerformanceResponse)
                .toList();
    }

}