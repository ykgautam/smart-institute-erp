package com.smartinstitute.erp.report.service.impl;

import com.smartinstitute.erp.common.service.BaseCrudService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.report.dto.request.StudentPerformanceReportRequest;
import com.smartinstitute.erp.report.dto.response.StudentPerformanceReportPageResponse;
import com.smartinstitute.erp.report.dto.response.StudentPerformanceReportResponse;
import com.smartinstitute.erp.report.projection.StudentPerformanceReportProjection;
import com.smartinstitute.erp.report.repository.StudentPerformanceReportRepository;
import com.smartinstitute.erp.report.service.StudentPerformanceReportService;
import com.smartinstitute.erp.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StudentPerformanceReportServiceImpl
    extends BaseCrudService
        implements StudentPerformanceReportService {

    private final StudentPerformanceReportRepository
            studentPerformanceReportRepository;

    public StudentPerformanceReportServiceImpl(
            SecurityUtil securityUtil,
            InstituteAccessValidator instituteAccessValidator,
            StudentPerformanceReportRepository studentPerformanceReportRepository

    ) {
        super(securityUtil, instituteAccessValidator);
        this.studentPerformanceReportRepository = studentPerformanceReportRepository;
    }

    /**
     * Fetches student academic performance report.
     *
     * <p>
     * The service layer is responsible for:
     * </p>
     *
     * <ul>
     *     <li>Creating the pageable request.</li>
     *     <li>Applying sorting.</li>
     *     <li>Calling the repository.</li>
     *     <li>Mapping projections into API responses.</li>
     *     <li>Building the final paginated response.</li>
     * </ul>
     */
    @Override
    public StudentPerformanceReportPageResponse
    getStudentPerformanceReport(
            StudentPerformanceReportRequest request) {

        Institute institute = getCurrentInstitute();
        Pageable pageable = buildPageable(request);

        Page<StudentPerformanceReportProjection> page =
                studentPerformanceReportRepository
                        .getStudentPerformanceReportWithSorting(
                                institute.getId(),
                                request.getStudentId(),
                                request.getCourseId(),
                                request.getBatchId(),
                                request.getSubmittedFrom(),
                                request.getSubmittedTo(),
                                pageable,
                                request.getSortBy(),
                                request.getSortDirection()
                        );

        return mapToPageResponse(page);
    }

    /**
     * Creates Spring Data pagination and sorting metadata.
     *
     * <p>
     * Sorting is validated again at repository level because
     * the repository generates the native SQL ORDER BY clause.
     * </p>
     */
    private Pageable buildPageable(
            StudentPerformanceReportRequest request) {

        Sort.Direction direction =
                "DESC".equalsIgnoreCase(
                        request.getSortDirection()
                )
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        String sortBy =
                request.getSortBy() == null
                        || request.getSortBy().isBlank()
                        ? "studentName"
                        : request.getSortBy();

        return PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(direction, sortBy)
        );
    }

    /**
     * Converts repository projection results into
     * the API response structure.
     */
    private StudentPerformanceReportPageResponse
    mapToPageResponse(
            Page<StudentPerformanceReportProjection> page) {

        return new StudentPerformanceReportPageResponse(
                page.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList(),

                page.getTotalElements(),

                page.getTotalPages(),

                page.getNumber(),

                page.getSize(),

                page.isFirst(),

                page.isLast()
        );
    }

    /**
     * Maps one database projection into the API DTO.
     */
    private StudentPerformanceReportResponse
    mapToResponse(
            StudentPerformanceReportProjection projection) {

        return new StudentPerformanceReportResponse(
                projection.getStudentId(),
                projection.getStudentName(),
                projection.getCourseName(),
                projection.getBatchName(),
                projection.getTotalAttempts(),
                projection.getSubmittedAttempts(),
                projection.getPassedAttempts(),
                projection.getFailedAttempts(),
                projection.getAveragePercentage(),
                projection.getHighestPercentage(),
                projection.getLowestPercentage()
        );
    }
}