package com.smartinstitute.erp.report.service.impl;

import com.smartinstitute.erp.common.exception.BadRequestException;
import com.smartinstitute.erp.common.service.BaseCrudService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.report.dto.request.TestWisePerformanceReportRequest;
import com.smartinstitute.erp.report.dto.response.TestWisePerformanceReportPageResponse;
import com.smartinstitute.erp.report.dto.response.TestWisePerformanceReportResponse;
import com.smartinstitute.erp.report.projection.TestWisePerformanceReportProjection;
import com.smartinstitute.erp.report.repository.TestWisePerformanceReportRepository;
import com.smartinstitute.erp.report.service.TestWisePerformanceReportService;
import com.smartinstitute.erp.security.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class TestWisePerformanceReportServiceImpl
        extends BaseCrudService
        implements TestWisePerformanceReportService {

    private final TestWisePerformanceReportRepository
            testWisePerformanceReportRepository;

    public TestWisePerformanceReportServiceImpl(
            SecurityUtil securityUtil,
            InstituteAccessValidator instituteAccessValidator,
            TestWisePerformanceReportRepository
                    testWisePerformanceReportRepository) {

        super(
                securityUtil,
                instituteAccessValidator
        );

        this.testWisePerformanceReportRepository =
                testWisePerformanceReportRepository;
    }

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of(
                    "testName",
                    "courseName",
                    "topicName",
                    "totalAttempts",
                    "submittedAttempts",
                    "passedAttempts",
                    "failedAttempts",
                    "averagePercentage",
                    "highestPercentage",
                    "lowestPercentage"
            );

    @Override
    public TestWisePerformanceReportPageResponse
    getTestWisePerformanceReport(
            TestWisePerformanceReportRequest request) {

        validateRequest(request);

        Institute institute = getCurrentInstitute();

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize()
        );

        Page<TestWisePerformanceReportProjection> page =
                testWisePerformanceReportRepository
                        .getTestWisePerformanceReport(
                                institute.getId(),
                                request.getCourseId(),
                                request.getBatchId(),
                                request.getTestId(),
                                request.getSubmittedFrom(),
                                request.getSubmittedTo(),
                                pageable
                        );

        List<TestWisePerformanceReportResponse> content =
                page.getContent()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return TestWisePerformanceReportPageResponse
                .builder()
                .content(content)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    private TestWisePerformanceReportResponse toResponse(
            TestWisePerformanceReportProjection projection) {

        return TestWisePerformanceReportResponse
                .builder()
                .testId(projection.getTestId())
                .testName(projection.getTestName())
                .courseName(projection.getCourseName())
                .topicName(projection.getTopicName())
                .totalAttempts(
                        projection.getTotalAttempts() == null
                                ? 0L
                                : projection.getTotalAttempts()
                )
                .submittedAttempts(
                        projection.getSubmittedAttempts() == null
                                ? 0L
                                : projection.getSubmittedAttempts()
                )
                .passedAttempts(
                        projection.getPassedAttempts() == null
                                ? 0L
                                : projection.getPassedAttempts()
                )
                .failedAttempts(
                        projection.getFailedAttempts() == null
                                ? 0L
                                : projection.getFailedAttempts()
                )
                .averagePercentage(
                        projection.getAveragePercentage()
                )
                .highestPercentage(
                        projection.getHighestPercentage()
                )
                .lowestPercentage(
                        projection.getLowestPercentage()
                )
                .build();
    }

    private void validateRequest(
            TestWisePerformanceReportRequest request) {

        if (request == null) {
            throw new BadRequestException(
                    "Report request must not be null."
            );
        }

        if (request.getPage() == null ||
                request.getPage() < 0) {

            throw new BadRequestException(
                    "Page must be greater than or equal to 0."
            );
        }

        if (request.getSize() == null ||
                request.getSize() <= 0) {

            throw new BadRequestException(
                    "Page size must be greater than 0."
            );
        }

        if (request.getSize() > 100) {

            throw new BadRequestException(
                    "Page size must not exceed 100."
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

        if (request.getTestId() != null &&
                request.getTestId() <= 0) {

            throw new BadRequestException(
                    "Test ID must be greater than 0."
            );
        }

        LocalDateTime submittedFrom =
                request.getSubmittedFrom();

        LocalDateTime submittedTo =
                request.getSubmittedTo();

        if (submittedFrom != null &&
                submittedTo != null &&
                submittedFrom.isAfter(submittedTo)) {

            throw new BadRequestException(
                    "Submitted from must not be after submitted to."
            );
        }

        validateSort(request);
    }

    private void validateSort(
            TestWisePerformanceReportRequest request) {

        String sortBy = request.getSortBy();

        if (sortBy != null &&
                !sortBy.isBlank() &&
                !ALLOWED_SORT_FIELDS.contains(sortBy)) {

            throw new BadRequestException(
                    "Invalid sort field: " + sortBy
            );
        }

        String sortDirection =
                request.getSortDirection();

        if (sortDirection != null &&
                !sortDirection.isBlank() &&
                !sortDirection.equalsIgnoreCase("ASC") &&
                !sortDirection.equalsIgnoreCase("DESC")) {

            throw new BadRequestException(
                    "Sort direction must be ASC or DESC."
            );
        }
    }
}