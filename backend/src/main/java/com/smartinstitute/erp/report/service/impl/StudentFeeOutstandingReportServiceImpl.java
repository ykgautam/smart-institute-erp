package com.smartinstitute.erp.report.service.impl;


import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.report.dto.request.StudentFeeOutstandingReportRequest;
import com.smartinstitute.erp.report.dto.response.StudentFeeOutstandingReportPageResponse;
import com.smartinstitute.erp.report.dto.response.StudentFeeOutstandingReportResponse;
import com.smartinstitute.erp.report.dto.response.StudentFeeOutstandingReportSummaryResponse;
import com.smartinstitute.erp.report.projection.StudentFeeOutstandingReportProjection;
import com.smartinstitute.erp.report.repository.StudentFeeOutstandingReportRepository;
import com.smartinstitute.erp.report.service.StudentFeeOutstandingReportService;

import com.smartinstitute.erp.common.service.BaseCrudService;
import com.smartinstitute.erp.security.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Service implementation for Student Fee Outstanding Report.
 *
 * <p>
 * Responsible for:
 * <ul>
 *     <li>Resolving the current institute</li>
 *     <li>Validating institute access</li>
 *     <li>Applying pagination and sorting</li>
 *     <li>Fetching outstanding fee records</li>
 *     <li>Fetching overall financial summary</li>
 *     <li>Mapping repository results to API response DTOs</li>
 * </ul>
 * </p>
 */
@Service
@Transactional(readOnly = true)
public class StudentFeeOutstandingReportServiceImpl
        extends BaseCrudService
        implements StudentFeeOutstandingReportService {

    private final StudentFeeOutstandingReportRepository
            studentFeeOutstandingReportRepository;

    public StudentFeeOutstandingReportServiceImpl(
            SecurityUtil securityUtil,
            InstituteAccessValidator instituteAccessValidator,
            StudentFeeOutstandingReportRepository
                    studentFeeOutstandingReportRepository) {

        super(
                securityUtil,
                instituteAccessValidator
        );

        this.studentFeeOutstandingReportRepository =
                studentFeeOutstandingReportRepository;
    }

    /**
     * Generates the Student Fee Outstanding Report.
     *
     * <p>
     * The report contains two independent pieces of information:
     * </p>
     *
     * <ol>
     *     <li>
     *         Paginated outstanding fee records
     *     </li>
     *     <li>
     *         Overall financial summary
     *     </li>
     * </ol>
     */
    @Override
    public StudentFeeOutstandingReportPageResponse
    getStudentFeeOutstandingReport(
            StudentFeeOutstandingReportRequest request) {

        /*
         * Resolve the institute from the authenticated user context.
         *
         * The instituteId supplied by the client is intentionally
         * not trusted for tenant isolation.
         */
        Institute institute =
                getCurrentInstitute();

        Long instituteId =
                institute.getId();

        /*
         * Build pagination and sorting information.
         */
        Sort.Direction direction =
                "DESC".equalsIgnoreCase(
                        request.getSortDirection()
                )
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        Pageable pageable =
                PageRequest.of(
                        request.getPage(),
                        request.getSize(),
                        Sort.by(
                                direction,
                                request.getSortBy() != null
                                        ? request.getSortBy()
                                        : "pendingAmount"
                        )
                );

        /*
         * Convert enum to String because the native SQL query
         * compares the PostgreSQL VARCHAR enum value.
         */
        String feeStatus =
                request.getFeeStatus() != null
                        ? request.getFeeStatus().name()
                        : null;

        /*
         * Fetch paginated outstanding fee records.
         */
        Page<StudentFeeOutstandingReportProjection> page =
                studentFeeOutstandingReportRepository
                        .getStudentFeeOutstandingReport(
                                instituteId,
                                request.getCourseId(),
                                request.getBatchId(),
                                feeStatus,
                                request.getDueDateFrom(),
                                request.getDueDateTo(),
                                pageable,
                                request.getSortBy(),
                                request.getSortDirection()
                        );

        /*
         * Map paginated projection records to response DTOs.
         */
        List<StudentFeeOutstandingReportResponse> content =
                page.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        /*
         * Fetch summary separately.
         *
         * Summary is NOT calculated from page.getContent()
         * because that would make the summary dependent on
         * pagination.
         */
        Object[] summaryRow =
                studentFeeOutstandingReportRepository
                        .getStudentFeeOutstandingReportSummary(
                                instituteId,
                                request.getCourseId(),
                                request.getBatchId(),
                                feeStatus,
                                request.getDueDateFrom(),
                                request.getDueDateTo()
                        );

        StudentFeeOutstandingReportSummaryResponse summary =
                mapToSummaryResponse(summaryRow);

        /*
         * Build final paginated API response.
         */
        return new StudentFeeOutstandingReportPageResponse(
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
     * Maps the outstanding fee projection to API response DTO.
     */
    private StudentFeeOutstandingReportResponse
    mapToResponse(
            StudentFeeOutstandingReportProjection projection) {

        return new StudentFeeOutstandingReportResponse(
                projection.getStudentId(),
                projection.getStudentName(),
                projection.getCourseName(),
                projection.getBatchName(),
                projection.getTotalFee(),
                projection.getDiscount(),
                projection.getFinalFee(),
                projection.getPaidAmount(),
                projection.getPendingAmount(),
                projection.getFeeStatus(),
                projection.getFeeDueDate()
        );
    }

    /**
     * Maps the native SQL summary result to the summary DTO.
     *
     * <p>
     * Native SQL returns an Object[] in the following order:
     * </p>
     *
     * <pre>
     * 0 -> totalStudents
     * 1 -> studentsWithOutstandingFees
     * 2 -> totalFee
     * 3 -> totalDiscount
     * 4 -> totalFinalFee
     * 5 -> totalPaidAmount
     * 6 -> totalPendingAmount
     * </pre>
     */
    private StudentFeeOutstandingReportSummaryResponse
    mapToSummaryResponse(Object[] row) {

        return new StudentFeeOutstandingReportSummaryResponse(
                toLong(row[0]),
                toLong(row[1]),
                toBigDecimal(row[2]),
                toBigDecimal(row[3]),
                toBigDecimal(row[4]),
                toBigDecimal(row[5]),
                toBigDecimal(row[6])
        );
    }

    /**
     * Safely converts native SQL numeric values to Long.
     */
    private Long toLong(Object value) {

        if (value == null) {
            return 0L;
        }

        return ((Number) value).longValue();
    }

    /**
     * Safely converts native SQL numeric values to BigDecimal.
     */
    private BigDecimal toBigDecimal(Object value) {

        if (value == null) {
            return BigDecimal.ZERO;
        }

        if (value instanceof BigDecimal decimal) {
            return decimal;
        }

        return new BigDecimal(
                value.toString()
        );
    }
}