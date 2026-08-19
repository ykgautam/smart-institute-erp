package com.smartinstitute.erp.report.service.impl;

import com.smartinstitute.erp.common.service.BaseCrudService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.report.dto.request.StudentAttendanceReportRequest;
import com.smartinstitute.erp.report.dto.response.StudentAttendanceReportPageResponse;
import com.smartinstitute.erp.report.dto.response.StudentAttendanceReportResponse;
import com.smartinstitute.erp.report.dto.response.StudentAttendanceReportSummaryResponse;
import com.smartinstitute.erp.report.projection.StudentAttendanceReportProjection;
import com.smartinstitute.erp.report.repository.StudentAttendanceReportRepository;
import com.smartinstitute.erp.report.service.StudentAttendanceReportService;
import com.smartinstitute.erp.security.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the Student Attendance Report service.
 *
 * <p>
 * This service coordinates between the controller and the reporting
 * repository.
 * </p>
 *
 * <p>
 * The current institute is resolved through {@link BaseCrudService}.
 * This ensures that administrators cannot request attendance data
 * belonging to another institute by manipulating an institute ID
 * in the request.
 * </p>
 */
@Service
@Transactional(readOnly = true)
public class StudentAttendanceReportServiceImpl
        extends BaseCrudService
        implements StudentAttendanceReportService {

    private final StudentAttendanceReportRepository
            studentAttendanceReportRepository;

    /**
     * Creates the Student Attendance Report service.
     *
     * @param securityUtil security context utility
     * @param instituteAccessValidator validates institute access
     * @param studentAttendanceReportRepository attendance reporting repository
     */
    public StudentAttendanceReportServiceImpl(
            SecurityUtil securityUtil,
            InstituteAccessValidator instituteAccessValidator,
            StudentAttendanceReportRepository
                    studentAttendanceReportRepository) {

        /*
         * BaseCrudService handles common security and institute
         * related functionality.
         */
        super(
                securityUtil,
                instituteAccessValidator
        );

        this.studentAttendanceReportRepository =
                studentAttendanceReportRepository;
    }

    /**
     * Fetches the paginated Student Attendance Report.
     *
     * <p>
     * The current institute is obtained from the authenticated
     * user's context using getCurrentInstitute().
     * </p>
     *
     * <p>
     * Request filters such as course, batch, student and attendance
     * date range are passed to the repository.
     * </p>
     *
     * @param request report filters, pagination and sorting
     * @return paginated attendance report
     */
    @Override
    public StudentAttendanceReportPageResponse
    getStudentAttendanceReport(
            StudentAttendanceReportRequest request) {

        /*
         * Resolve the institute from the authenticated user's context.
         *
         * Do NOT take instituteId from the request because that would
         * allow a client to attempt accessing another institute's data.
         */
        Institute institute =
                getCurrentInstitute();

        Long instituteId =
                institute.getId();

        /*
         * Create the requested sort definition.
         *
         * The actual SQL column/expression is still protected by the
         * whitelist inside StudentAttendanceReportRepositoryImpl.
         */
        Sort sort =
                buildSort(request);

        /*
         * Create Spring Data pagination information.
         */
        Pageable pageable =
                PageRequest.of(
                        request.getPage(),
                        request.getSize(),
                        sort
                );

        /*
         * Fetch aggregated attendance data from the repository.
         */
        Page<StudentAttendanceReportProjection> page =
                studentAttendanceReportRepository
                        .getStudentAttendanceReport(
                                instituteId,
                                request.getCourseId(),
                                request.getBatchId(),
                                request.getStudentId(),
                                request.getDateFrom(),
                                request.getDateTo(),
                                pageable,
                                request.getSortBy(),
                                request.getSortDirection()
                        );

        /*
         * Convert repository projections into API response DTOs.
         *
         * Repository projections represent database/reporting data.
         * They should not be exposed directly through the REST API.
         */
        List<StudentAttendanceReportResponse> content =
                page.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        StudentAttendanceReportSummaryResponse summary =
                studentAttendanceReportRepository
                        .getSummary(
                                instituteId,
                                request.getCourseId(),
                                request.getBatchId(),
                                request.getStudentId(),
                                request.getDateFrom(),
                                request.getDateTo()
                        );

        /*
         * Build the final paginated response expected by the API.
         */
        return new StudentAttendanceReportPageResponse(
                content,
                summary,
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize(),
                page.isFirst(),
                page.isLast()
        );
    }

    /**
     * Builds the Spring Data sort definition.
     *
     * <p>
     * The service applies a safe default when the client does not
     * provide sorting information.
     * </p>
     *
     * <p>
     * The repository performs the final SQL sort-field whitelist.
     * </p>
     */
    private Sort buildSort(
            StudentAttendanceReportRequest request) {

        /*
         * Default sorting:
         *
         * Students are displayed alphabetically by first name.
         */
        String sortBy =
                request.getSortBy();

        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "studentName";
        }

        /*
         * Default sorting direction is ASC.
         */
        Sort.Direction direction =
                "DESC".equalsIgnoreCase(
                        request.getSortDirection()
                )
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        /*
         * Pageable sorting is mainly used to represent the requested
         * sorting configuration.
         *
         * The custom native repository resolves the actual SQL
         * expression through its own whitelist.
         */
        return Sort.by(
                direction,
                sortBy
        );
    }

    /**
     * Converts a repository projection into an API response DTO.
     *
     * @param projection database/reporting projection
     * @return API response object
     */
    private StudentAttendanceReportResponse mapToResponse(
            StudentAttendanceReportProjection projection) {

        return new StudentAttendanceReportResponse(
                projection.getStudentId(),
                projection.getStudentName(),
                projection.getCourseName(),
                projection.getBatchName(),
                projection.getTotalClasses(),
                projection.getPresentClasses(),
                projection.getAbsentClasses(),
                projection.getAttendancePercentage()
        );
    }
}