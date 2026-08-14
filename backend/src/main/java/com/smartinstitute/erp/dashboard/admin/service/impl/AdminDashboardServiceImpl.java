package com.smartinstitute.erp.dashboard.admin.service.impl;

import com.smartinstitute.erp.attendance.repository.AttendanceRepository;
import com.smartinstitute.erp.batch.repository.BatchRepository;
import com.smartinstitute.erp.common.enums.attendance.AttendanceStatus;
import com.smartinstitute.erp.common.enums.fee.FeeStatus;
import com.smartinstitute.erp.common.enums.test.TestStatus;
import com.smartinstitute.erp.common.service.BaseCrudService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.course.repository.CourseRepository;
import com.smartinstitute.erp.dashboard.admin.dto.response.*;
import com.smartinstitute.erp.dashboard.admin.projection.AttendanceTrendProjection;
import com.smartinstitute.erp.dashboard.admin.projection.FeeCollectionTrendProjection;
import com.smartinstitute.erp.dashboard.admin.projection.StudentGrowthProjection;
import com.smartinstitute.erp.dashboard.admin.service.AdminDashboardService;
import com.smartinstitute.erp.dashboard.dto.response.CoursePerformanceResponse;
import com.smartinstitute.erp.dashboard.projection.CoursePerformanceProjection;
import com.smartinstitute.erp.fee.repository.FeePaymentRepository;
import com.smartinstitute.erp.fee.repository.StudentFeeRepository;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.security.util.SecurityUtil;
import com.smartinstitute.erp.student.repository.StudentRepository;

import com.smartinstitute.erp.test.repository.StudentTestRepository;
import com.smartinstitute.erp.test.repository.TestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdminDashboardServiceImpl extends BaseCrudService
        implements AdminDashboardService {

    private final StudentRepository studentRepository;

    private final CourseRepository courseRepository;

    private final BatchRepository batchRepository;

    private final AttendanceRepository attendanceRepository;

    private final StudentFeeRepository studentFeeRepository;

    private final FeePaymentRepository feePaymentRepository;

    private final TestRepository testRepository;

    private final StudentTestRepository studentTestRepository;

    public AdminDashboardServiceImpl(
            SecurityUtil securityUtil,
            InstituteAccessValidator instituteAccessValidator,
            StudentRepository studentRepository,
            CourseRepository courseRepository,
            BatchRepository batchRepository,
            AttendanceRepository attendanceRepository,
            StudentFeeRepository studentFeeRepository,
            FeePaymentRepository feePaymentRepository,
            TestRepository testRepository,
            StudentTestRepository studentTestRepository) {

        super(
                securityUtil,
                instituteAccessValidator
        );

        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.batchRepository = batchRepository;
        this.attendanceRepository = attendanceRepository;
        this.studentFeeRepository = studentFeeRepository;
        this.feePaymentRepository = feePaymentRepository;
        this.testRepository = testRepository;
        this.studentTestRepository = studentTestRepository;
    }


    @Override
    public AdminDashboardResponse getDashboard() {

        Institute institute = getCurrentInstitute();

        return AdminDashboardResponse.builder()

                .totalStudents(getTotalStudents(institute))

                .activeStudents(getActiveStudents(institute))

                .totalCourses(getTotalCourses(institute))

                .totalBatches(getTotalBatches(institute))

                .todayAttendancePercentage(getTodayAttendancePercentage(institute))

                .pendingFeeStudents(getPendingFeeStudents(institute))

                .todayCollection(getTodayCollection(institute))

                .upcomingTests(getUpcomingTests(institute))

                .build();
    }

    @Override
    public List<StudentGrowthResponse> getStudentGrowth() {

        Institute institute = getCurrentInstitute();

        return studentRepository
                .getStudentGrowth(institute)
                .stream()
                .map(this::toStudentGrowthResponse)
                .toList();
    }

    private StudentGrowthResponse toStudentGrowthResponse(
            StudentGrowthProjection projection) {

        return StudentGrowthResponse.builder()
                .year(projection.getYear())
                .month(projection.getMonth())
                .studentCount(projection.getStudentCount())
                .build();
    }

    private Integer getTotalStudents(Institute institute) {
        return (int) studentRepository.countByInstitute(institute);
    }

    private Integer getActiveStudents(Institute institute) {
        return (int) studentRepository.countByInstituteAndActiveTrue(institute);
    }

    private Integer getTotalCourses(
            Institute institute) {

        return (int) courseRepository.countByInstitute(institute);
    }

    private Integer getTotalBatches(Institute institute) {

        return (int) batchRepository.countByInstitute(institute);
    }

    private Integer getPendingFeeStudents1(Institute institute) {

        return (int) studentFeeRepository
                .countByInstituteAndStatus(
                        institute,
                        FeeStatus.PENDING
                );
    }

    private Integer getPendingFeeStudents(Institute institute) {

        long pending =
                studentFeeRepository.countByInstituteAndStatus(
                        institute,
                        FeeStatus.PENDING
                );

        long partiallyPaid =
                studentFeeRepository.countByInstituteAndStatus(
                        institute,
                        FeeStatus.PARTIALLY_PAID
                );

        return (int) (pending + partiallyPaid);
    }

    private BigDecimal getTodayCollection(Institute institute) {

        BigDecimal collection =
                feePaymentRepository.getCollectionForDate(
                        institute,
                        LocalDate.now()
                );

        return collection == null
                ? BigDecimal.ZERO
                : collection;
    }

    private Double getTodayAttendancePercentage(Institute institute) {

        LocalDate today = LocalDate.now();

        long totalAttendance =
                attendanceRepository.countByBatch_InstituteAndAttendanceDate(
                        institute,
                        today
                );

        if (totalAttendance == 0) {
            return 0.0;
        }

        long presentStudents =
                attendanceRepository
                        .countByBatch_InstituteAndAttendanceDateAndStatus(
                                institute,
                                today,
                                AttendanceStatus.PRESENT
                        );

        return (presentStudents * 100.0) / totalAttendance;
    }

    private Integer getUpcomingTests(Institute institute) {

        return (int) testRepository
                .countByInstituteAndStatusAndActiveTrueAndStartTimeAfter(
                        institute,
                        TestStatus.PUBLISHED,
                        LocalDateTime.now()
                );
    }

    @Override
    public List<AttendanceTrendResponse> getAttendanceTrend() {

        Institute institute = getCurrentInstitute();

        return attendanceRepository
                .getAttendanceTrend(institute)
                .stream()
                .map(this::toAttendanceTrendResponse)
                .toList();
    }

    private AttendanceTrendResponse toAttendanceTrendResponse(
            AttendanceTrendProjection projection) {

        return AttendanceTrendResponse.builder()
                .year(projection.getYear())
                .month(projection.getMonth())
                .attendancePercentage(projection.getAttendancePercentage())
                .build();
    }

    @Override
    public List<FeeCollectionTrendResponse> getFeeCollectionTrend() {
        Institute institute = getCurrentInstitute();
        return feePaymentRepository
                .getFeeCollectionTrend(institute)
                .stream()
                .map(this::toFeeCollectionTrendResponse)
                .toList();
    }

    private FeeCollectionTrendResponse toFeeCollectionTrendResponse(
            FeeCollectionTrendProjection projection) {

        return FeeCollectionTrendResponse.builder()
                .year(projection.getYear())
                .month(projection.getMonth())
                .totalCollection(projection.getTotalCollection())
                .build();
    }

    @Override
    public List<CoursePerformanceResponse> getCoursePerformance() {

        Institute institute = getCurrentInstitute();

        return studentTestRepository
                .getCoursePerformance(institute)
                .stream()
                .map(this::toCoursePerformanceResponse)
                .toList();
    }

    private CoursePerformanceResponse toCoursePerformanceResponse(
            CoursePerformanceProjection projection) {

        return CoursePerformanceResponse.builder()

                .courseId(projection.getCourseId())

                .courseName(projection.getCourseName())

                .testsAttempted(projection.getTestsAttempted().intValue())

                .testsPassed(projection.getTestsPassed().intValue())

                .averagePercentage(projection.getAveragePercentage())

                .passPercentage(projection.getPassPercentage())

                .build();
    }

    @Override
    public AdminAnalyticsResponse getAnalyticsSummary() {

        return AdminAnalyticsResponse.builder()

                .studentGrowth(getStudentGrowth())

                .attendanceTrend(getAttendanceTrend())

                .feeCollectionTrend(getFeeCollectionTrend())

                .coursePerformance(getCoursePerformance())

                .build();
    }
}