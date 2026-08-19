package com.smartinstitute.erp.report.service.impl;

import com.smartinstitute.erp.common.service.BaseCrudService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.report.dto.request.CoursePerformanceReportRequest;
import com.smartinstitute.erp.report.dto.response.CoursePerformanceReportPageResponse;
import com.smartinstitute.erp.report.dto.response.CoursePerformanceReportResponse;
import com.smartinstitute.erp.report.projection.CoursePerformanceReportProjection;
import com.smartinstitute.erp.report.repository.CoursePerformanceReportRepository;
import com.smartinstitute.erp.report.service.CoursePerformanceReportService;
import com.smartinstitute.erp.security.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for the Course Performance Report.
 *
 * <p>
 * Responsible for:
 * </p>
 *
 * <ul>
 *     <li>Creating pagination and sorting configuration</li>
 *     <li>Passing report filters to the repository</li>
 *     <li>Mapping repository projections to API responses</li>
 *     <li>Building the paginated response</li>
 * </ul>
 */
@Service
@Transactional(readOnly = true)
public class CoursePerformanceReportServiceImpl
        extends BaseCrudService
        implements CoursePerformanceReportService {

    private final CoursePerformanceReportRepository coursePerformanceReportRepository;

    public CoursePerformanceReportServiceImpl(
            SecurityUtil securityUtil,
            InstituteAccessValidator instituteAccessValidator,
            CoursePerformanceReportRepository
                    coursePerformanceReportRepository) {
        super(securityUtil, instituteAccessValidator);
        this.coursePerformanceReportRepository = coursePerformanceReportRepository;
    }

    /**
     * Fetches the course-wise academic performance report.
     *
     * @param request report request containing filters,
     *                pagination and sorting information
     * @return paginated course performance report
     */
    @Override
    public CoursePerformanceReportPageResponse
    getCoursePerformanceReport(
            CoursePerformanceReportRequest request) {

        Institute institute = getCurrentInstitute();

        /*
         * Build dynamic sorting.
         *
         * The repository performs the actual safe sort-column
         * resolution. Here we only create the Pageable object.
         */
        Sort.Direction direction =
                "DESC".equalsIgnoreCase(
                        request.getSortDirection())
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        Pageable pageable =
                PageRequest.of(
                        request.getPage(),
                        request.getSize(),
                        Sort.by(
                                direction,
                                request.getSortBy()
                        )
                );

        /*
         * Fetch aggregated course performance data
         * from the custom repository implementation.
         */
        Page<CoursePerformanceReportProjection> page =
                coursePerformanceReportRepository
                        .getCoursePerformanceReportWithSorting(
                                institute.getId(),
                                request.getCourseId(),
                                request.getSubmittedFrom(),
                                request.getSubmittedTo(),
                                pageable,
                                request.getSortBy(),
                                request.getSortDirection()
                        );

        /*
         * Convert database projections into API response DTOs.
         */
        Page<CoursePerformanceReportResponse> responsePage =
                page.map(this::mapToResponse);

        /*
         * Build the standard paginated response used
         * throughout the reporting module.
         */
        return new CoursePerformanceReportPageResponse(
                responsePage.getContent(),
                responsePage.getTotalElements(),
                responsePage.getTotalPages(),
                responsePage.getNumber(),
                responsePage.getSize(),
                responsePage.isFirst(),
                responsePage.isLast()
        );
    }

    /**
     * Maps repository projection to API response DTO.
     */
    private CoursePerformanceReportResponse mapToResponse(
            CoursePerformanceReportProjection projection) {

        return new CoursePerformanceReportResponse(
                projection.getCourseId(),
                projection.getCourseName(),
                projection.getTotalStudents(),
                projection.getStudentsAttempted(),
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