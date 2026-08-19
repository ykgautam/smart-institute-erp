package com.smartinstitute.erp.report.service.impl;

import com.smartinstitute.erp.common.exception.BadRequestException;
import com.smartinstitute.erp.common.service.BaseCrudService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.report.dto.response.StudentReportPageResponse;
import com.smartinstitute.erp.report.dto.response.StudentReportResponse;
import com.smartinstitute.erp.report.dto.response.StudentReportSummaryResponse;
import com.smartinstitute.erp.report.projection.StudentReportProjection;
import com.smartinstitute.erp.report.projection.StudentReportSummaryProjection;
import com.smartinstitute.erp.report.repository.StudentReportRepository;
import com.smartinstitute.erp.report.service.StudentReportService;
import com.smartinstitute.erp.security.util.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.smartinstitute.erp.report.dto.request.StudentReportRequest;

import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StudentReportServiceImpl
        extends BaseCrudService
        implements StudentReportService {

    private final StudentReportRepository studentReportRepository;

    public StudentReportServiceImpl(
            SecurityUtil securityUtil,
            InstituteAccessValidator instituteAccessValidator,
            StudentReportRepository studentReportRepository) {

        super(
                securityUtil,
                instituteAccessValidator
        );

        this.studentReportRepository = studentReportRepository;
    }

    @Override
    public StudentReportPageResponse getStudentReport(
            StudentReportRequest request) {

        validateRequest(request);

        Institute institute = getCurrentInstitute();

        String status = request.getStatus() == null
                ? null
                : request.getStatus().name();

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize()
        );

        Page<StudentReportProjection> page =
                studentReportRepository.getStudentReport(
                        institute.getId(),
                        request.getCourseId(),
                        request.getBatchId(),
                        status,
                        request.getAdmissionDateFrom(),
                        request.getAdmissionDateTo(),
                        pageable
                );

        List<StudentReportSummaryProjection> summaryProjections =
                studentReportRepository.getStudentReportSummary(
                        institute.getId(),
                        request.getCourseId(),
                        request.getBatchId(),
                        status,
                        request.getAdmissionDateFrom(),
                        request.getAdmissionDateTo()
                );

        StudentReportSummaryResponse summary =
                buildStudentReportSummary(summaryProjections);

        List<StudentReportResponse> content =
                page.getContent()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return StudentReportPageResponse.builder()
                .content(content)
                .summary(summary)
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    private StudentReportResponse toStudentReportResponse(
            StudentReportProjection projection) {

        return StudentReportResponse.builder()
                .studentId(projection.getStudentId())
                .studentName(projection.getStudentName())
                .email(projection.getEmail())
                .courseName(projection.getCourseName())
                .batchName(projection.getBatchName())
                .status(projection.getStatus())
                .build();
    }

    private StudentReportResponse toResponse(
            StudentReportProjection projection) {

        return StudentReportResponse.builder()

                .studentId(projection.getStudentId())

                .studentName(projection.getStudentName())

                .email(projection.getEmail())

                .courseName(projection.getCourseName())

                .batchName(projection.getBatchName())

                .status(projection.getStatus())

                .build();
    }

    private static final Map<String, String> SORT_FIELDS =
            Map.of(
                    "studentName", "studentName",
                    "email", "email",
                    "status", "status",
                    "admissionDate", "s.admission_date"
            );

    private Sort buildSort(StudentReportRequest request) {

        String sortBy = request.getSortBy();

        String mappedField = SORT_FIELDS.get(sortBy);

        if (mappedField == null) {
            throw new IllegalArgumentException(
                    "Invalid sort field: " + sortBy
            );
        }

        Sort.Direction direction =
                "desc".equalsIgnoreCase(request.getSortDirection())
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        return Sort.by(direction, mappedField);
    }

    private void validateRequest(StudentReportRequest request) {

        if (request.getPage() < 0) {
            throw new BadRequestException("Page must be greater than or equal to 0.");
        }

        if (request.getSize() <= 0) {
            throw new BadRequestException("Page size must be greater than 0.");
        }

        if (request.getSize() > 100) {
            throw new BadRequestException("Page size must not exceed 100.");
        }

        if (request.getCourseId() != null &&
                request.getCourseId() <= 0) {
            throw new BadRequestException("Course ID must be greater than 0.");
        }

        if (request.getBatchId() != null &&
                request.getBatchId() <= 0) {
            throw new BadRequestException("Batch ID must be greater than 0.");
        }

        if (request.getAdmissionDateFrom() != null &&
                request.getAdmissionDateTo() != null &&
                request.getAdmissionDateFrom()
                        .isAfter(request.getAdmissionDateTo())) {

            throw new BadRequestException(
                    "Admission date from must not be after admission date to."
            );
        }
    }

    private StudentReportSummaryResponse buildStudentReportSummary(
            List<StudentReportSummaryProjection> projections) {

        Map<String, Long> statusCounts =
                projections.stream()
                        .collect(
                                Collectors.toMap(
                                        StudentReportSummaryProjection::getStatus,
                                        StudentReportSummaryProjection::getCount
                                )
                        );

        long totalStudents =
                statusCounts.values()
                        .stream()
                        .mapToLong(Long::longValue)
                        .sum();

        return StudentReportSummaryResponse.builder()
                .totalStudents(totalStudents)
                .statusCounts(statusCounts)
                .build();
    }
}