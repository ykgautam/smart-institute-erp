package com.smartinstitute.erp.report.service.impl;

import com.smartinstitute.erp.common.exception.BadRequestException;
import com.smartinstitute.erp.common.service.BaseCrudService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.report.dto.request.StudentAcademicReportRequest;
import com.smartinstitute.erp.report.dto.response.StudentAcademicReportPageResponse;
import com.smartinstitute.erp.report.dto.response.StudentAcademicReportResponse;
import com.smartinstitute.erp.report.dto.response.StudentAcademicReportSummaryResponse;
import com.smartinstitute.erp.report.projection.StudentAcademicReportProjection;
import com.smartinstitute.erp.report.projection.StudentAcademicReportSummaryProjection;
import com.smartinstitute.erp.report.repository.StudentAcademicReportRepository;
import com.smartinstitute.erp.report.service.StudentAcademicReportService;
import com.smartinstitute.erp.security.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class StudentAcademicReportServiceImpl
        extends BaseCrudService
        implements StudentAcademicReportService {

    private final StudentAcademicReportRepository
            studentAcademicReportRepository;

    public StudentAcademicReportServiceImpl(
            SecurityUtil securityUtil,
            InstituteAccessValidator instituteAccessValidator,
            StudentAcademicReportRepository
                    studentAcademicReportRepository) {

        super(
                securityUtil,
                instituteAccessValidator
        );

        this.studentAcademicReportRepository =
                studentAcademicReportRepository;
    }

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of(
                    "studentName",
                    "courseName",
                    "batchName",
                    "attendancePercentage",
                    "totalTests",
                    "totalAttempts",
                    "passedTests",
                    "failedTests",
                    "averageTestPercentage"
            );

    private String resolveSortField(String sortBy) {

        if (sortBy == null || sortBy.isBlank()) {
            return "studentName";
        }

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new BadRequestException(
                    "Invalid sort field: " + sortBy
            );
        }

        return sortBy;
    }

    private String resolveSortDirection(String sortDirection) {

        if (sortDirection == null ||
                sortDirection.isBlank()) {

            return "ASC";
        }

        if (!"ASC".equalsIgnoreCase(sortDirection) &&
                !"DESC".equalsIgnoreCase(sortDirection)) {

            throw new BadRequestException(
                    "Sort direction must be ASC or DESC."
            );
        }

        return sortDirection.toUpperCase();
    }

    @Override
    public StudentAcademicReportPageResponse
    getStudentAcademicReport(
            StudentAcademicReportRequest request) {

        validateRequest(request);

        Institute institute =
                getCurrentInstitute();

        Pageable pageable =
                PageRequest.of(
                        request.getPage(),
                        request.getSize()
                );

        Page<StudentAcademicReportProjection> page =
                studentAcademicReportRepository
                        .getStudentAcademicReport(
                                institute.getId(),
                                request.getCourseId(),
                                request.getBatchId(),
                                request.getStudentId(),
                                pageable
                        );

        StudentAcademicReportSummaryProjection summaryProjection =
                studentAcademicReportRepository
                        .getStudentAcademicReportSummary(
                                institute.getId(),
                                request.getCourseId(),
                                request.getBatchId()
                        );

        StudentAcademicReportSummaryResponse summary =
                toSummaryResponse(summaryProjection);

        List<StudentAcademicReportResponse> content =
                page.getContent()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return StudentAcademicReportPageResponse.builder()
                .content(content)
                .summary(summary)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    private StudentAcademicReportSummaryResponse
    toSummaryResponse(StudentAcademicReportSummaryProjection projection) {

        return StudentAcademicReportSummaryResponse.builder()
                .totalStudents(
                        projection.getTotalStudents() == null
                                ? 0L
                                : projection.getTotalStudents()
                )
                .averageAttendancePercentage(
                        projection.getAverageAttendancePercentage() == null
                                ? BigDecimal.ZERO
                                : projection.getAverageAttendancePercentage()
                )
                .totalTests(
                        projection.getTotalTests() == null
                                ? 0L
                                : projection.getTotalTests()
                )
                .totalAttempts(
                        projection.getTotalAttempts() == null
                                ? 0L
                                : projection.getTotalAttempts()
                )
                .passedTests(
                        projection.getPassedTests() == null
                                ? 0L
                                : projection.getPassedTests()
                )
                .failedTests(
                        projection.getFailedTests() == null
                                ? 0L
                                : projection.getFailedTests()
                )
                .averageTestPercentage(
                        projection.getAverageTestPercentage() == null
                                ? BigDecimal.ZERO
                                : projection.getAverageTestPercentage()
                )
                .build();
    }

    private StudentAcademicReportResponse toResponse(
            StudentAcademicReportProjection projection) {

        return StudentAcademicReportResponse.builder()
                .studentId(projection.getStudentId())
                .studentName(projection.getStudentName())
                .courseName(projection.getCourseName())
                .batchName(projection.getBatchName())
                .attendancePercentage(
                        projection.getAttendancePercentage()
                )
                .totalTests(
                        projection.getTotalTests()
                )
                .totalAttempts(
                        projection.getTotalAttempts()
                )
                .passedTests(
                        projection.getPassedTests()
                )
                .failedTests(
                        projection.getFailedTests()
                )
                .averageTestPercentage(
                        projection.getAverageTestPercentage()
                )
                .build();
    }

    private void validateRequest(
            StudentAcademicReportRequest request) {

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

        if (request.getStudentId() != null &&
                request.getStudentId() <= 0) {

            throw new BadRequestException(
                    "Student ID must be greater than 0."
            );
        }

        String sortBy = resolveSortField(request.getSortBy());

        String sortDirection = resolveSortDirection(request.getSortDirection());

    }
}