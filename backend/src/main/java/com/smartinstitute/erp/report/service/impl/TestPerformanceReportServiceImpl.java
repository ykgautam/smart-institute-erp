package com.smartinstitute.erp.report.service.impl;

import com.smartinstitute.erp.common.exception.BadRequestException;
import com.smartinstitute.erp.common.service.BaseCrudService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.report.dto.request.TestPerformanceReportRequest;
import com.smartinstitute.erp.report.dto.response.TestPerformanceReportPageResponse;
import com.smartinstitute.erp.report.dto.response.TestPerformanceReportResponse;
import com.smartinstitute.erp.report.dto.response.TestPerformanceReportSummaryResponse;
import com.smartinstitute.erp.report.projection.TestPerformanceReportProjection;
import com.smartinstitute.erp.report.projection.TestPerformanceReportSummaryProjection;
import com.smartinstitute.erp.report.repository.TestPerformanceReportRepository;
import com.smartinstitute.erp.report.service.TestPerformanceReportService;
import com.smartinstitute.erp.security.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class TestPerformanceReportServiceImpl
        extends BaseCrudService
        implements TestPerformanceReportService {

    private final TestPerformanceReportRepository
            testPerformanceReportRepository;

    public TestPerformanceReportServiceImpl(
            SecurityUtil securityUtil,
            InstituteAccessValidator instituteAccessValidator,
            TestPerformanceReportRepository
                    testPerformanceReportRepository) {

        super(
                securityUtil,
                instituteAccessValidator
        );

        this.testPerformanceReportRepository =
                testPerformanceReportRepository;
    }

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of(
                    "studentName",
                    "testName",
                    "courseName",
                    "batchName",
                    "attemptNo",
                    "status",
                    "totalMarks",
                    "obtainedMarks",
                    "percentage",
                    "startedAt",
                    "submittedAt"
            );

    private String resolveSortColumn(String sortBy) {

        return switch (sortBy) {

            case "studentName" -> "s.first_name";

            case "testName" -> "t.title";

            case "courseName" -> "c.course_name";

            case "batchName" -> "b.batch_name";

            case "attemptNo" -> "st.attempt_no";

            case "status" -> "st.status";

            case "totalMarks" -> "st.total_marks";

            case "obtainedMarks" -> "st.obtained_marks";

            case "percentage" -> "st.percentage";

            case "startedAt" -> "st.started_at";

            case "submittedAt" -> "st.submitted_at";

            default -> "s.first_name";
        };
    }

    private String resolveSortDirection(String sortDirection) {

        if (sortDirection == null ||
                sortDirection.isBlank()) {

            return "ASC";
        }

        if ("asc".equalsIgnoreCase(sortDirection)) {
            return "ASC";
        }

        if ("desc".equalsIgnoreCase(sortDirection)) {
            return "DESC";
        }

        throw new BadRequestException(
                "Invalid sort direction: " + sortDirection
        );
    }

    @Override
    public TestPerformanceReportPageResponse getTestPerformanceReport(
            TestPerformanceReportRequest request) {

        validateRequest(request);

        Institute institute = getCurrentInstitute();

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize()
        );

        String status = request.getStatus() == null
                ? null
                : request.getStatus().name();

        Page<TestPerformanceReportProjection> page =
                testPerformanceReportRepository
                        .getTestPerformanceReport(
                                institute.getId(),
                                request.getTestId(),
                                request.getCourseId(),
                                request.getBatchId(),
                                request.getStudentId(),
                                status,
                                request.getSubmittedFrom(),
                                request.getSubmittedTo(),
                                pageable
                        );

        TestPerformanceReportSummaryProjection summaryProjection =
                testPerformanceReportRepository
                        .getTestPerformanceReportSummary(
                                institute.getId(),
                                request.getTestId(),
                                request.getCourseId(),
                                request.getBatchId(),
                                request.getStudentId(),
                                status,
                                request.getSubmittedFrom(),
                                request.getSubmittedTo()
                        );
        TestPerformanceReportSummaryResponse summary =
                toSummaryResponse(summaryProjection);

        return TestPerformanceReportPageResponse.builder()
                .content(
                        page.getContent()
                                .stream()
                                .map(this::toResponse)
                                .toList()
                )
                .summary(summary)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    private TestPerformanceReportResponse toResponse(
            TestPerformanceReportProjection projection) {

        return TestPerformanceReportResponse.builder()
                .studentTestId(projection.getStudentTestId())
                .studentId(projection.getStudentId())
                .studentName(projection.getStudentName())
                .testId(projection.getTestId())
                .testName(projection.getTestName())
                .courseName(projection.getCourseName())
                .batchName(projection.getBatchName())
                .attemptNo(projection.getAttemptNo())
                .status(projection.getStatus())
                .totalMarks(projection.getTotalMarks())
                .obtainedMarks(projection.getObtainedMarks())
                .percentage(projection.getPercentage())
                .passed(projection.getPassed())
                .startedAt(projection.getStartedAt())
                .submittedAt(projection.getSubmittedAt())
                .build();
    }

    private TestPerformanceReportSummaryResponse toSummaryResponse(
            TestPerformanceReportSummaryProjection projection) {

        return TestPerformanceReportSummaryResponse.builder()

                .totalAttempts(
                        projection.getTotalAttempts() == null
                                ? 0
                                : projection.getTotalAttempts()
                )

                .submittedAttempts(
                        projection.getSubmittedAttempts() == null
                                ? 0
                                : projection.getSubmittedAttempts()
                )

                .autoSubmittedAttempts(
                        projection.getAutoSubmittedAttempts() == null
                                ? 0
                                : projection.getAutoSubmittedAttempts()
                )

                .inProgressAttempts(
                        projection.getInProgressAttempts() == null
                                ? 0
                                : projection.getInProgressAttempts()
                )

                .passedAttempts(
                        projection.getPassedAttempts() == null
                                ? 0
                                : projection.getPassedAttempts()
                )

                .failedAttempts(
                        projection.getFailedAttempts() == null
                                ? 0
                                : projection.getFailedAttempts()
                )

                .averagePercentage(
                        projection.getAveragePercentage() == null
                                ? BigDecimal.ZERO
                                : projection.getAveragePercentage()
                )

                .totalMarks(
                        projection.getTotalMarks() == null
                                ? 0
                                : projection.getTotalMarks()
                )

                .totalObtainedMarks(
                        projection.getTotalObtainedMarks() == null
                                ? 0
                                : projection.getTotalObtainedMarks()
                )

                .build();
    }

    private void validateRequest(
            TestPerformanceReportRequest request) {

        if (request.getPage() < 0) {
            throw new BadRequestException(
                    "Page must be greater than or equal to 0."
            );
        }

        if (request.getSize() <= 0) {
            throw new BadRequestException(
                    "Page size must be greater than 0."
            );
        }

        if (request.getSize() > 100) {
            throw new BadRequestException(
                    "Page size must not exceed 100."
            );
        }

        if (request.getTestId() != null &&
                request.getTestId() <= 0) {

            throw new BadRequestException(
                    "Test ID must be greater than 0."
            );
        }

        if (request.getCourseId() != null &&
                request.getCourseId() <= 0) {

            throw new BadRequestException(
                    "Course ID must be greater than 0."
            );
        }

        if (request.getBatchId() != null &&
                request.getBatchId() <= 0) {

            throw new BadRequestException(
                    "Batch ID must be greater than 0."
            );
        }

        if (request.getStudentId() != null &&
                request.getStudentId() <= 0) {

            throw new BadRequestException(
                    "Student ID must be greater than 0."
            );
        }

        if (request.getSubmittedFrom() != null &&
                request.getSubmittedTo() != null &&
                request.getSubmittedFrom()
                        .isAfter(request.getSubmittedTo())) {

            throw new BadRequestException(
                    "Submitted from must not be after submitted to."
            );
        }
    }
}