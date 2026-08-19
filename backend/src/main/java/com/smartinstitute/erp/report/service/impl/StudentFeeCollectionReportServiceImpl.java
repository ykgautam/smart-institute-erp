package com.smartinstitute.erp.report.service.impl;


import com.smartinstitute.erp.common.service.BaseCrudService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.report.dto.request.StudentFeeCollectionReportRequest;
import com.smartinstitute.erp.report.dto.response.StudentFeeCollectionReportPageResponse;
import com.smartinstitute.erp.report.dto.response.StudentFeeCollectionReportResponse;
import com.smartinstitute.erp.report.dto.response.StudentFeeCollectionReportSummaryResponse;
import com.smartinstitute.erp.report.projection.StudentFeeCollectionReportProjection;
import com.smartinstitute.erp.report.repository.StudentFeeCollectionReportRepository;
import com.smartinstitute.erp.report.service.StudentFeeCollectionReportService;
import com.smartinstitute.erp.security.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class StudentFeeCollectionReportServiceImpl
        extends BaseCrudService
        implements StudentFeeCollectionReportService {

    private final StudentFeeCollectionReportRepository
            studentFeeCollectionReportRepository;

    public StudentFeeCollectionReportServiceImpl(
            SecurityUtil securityUtil,
            InstituteAccessValidator instituteAccessValidator,
            StudentFeeCollectionReportRepository
                    studentFeeCollectionReportRepository) {

        super(
                securityUtil,
                instituteAccessValidator
        );

        this.studentFeeCollectionReportRepository =
                studentFeeCollectionReportRepository;
    }

    /**
     * Generates the Student Fee Collection Report.
     *
     * <p>
     * The institute is obtained from the authenticated user context
     * through {@link BaseCrudService#getCurrentInstitute()}.
     * The instituteId supplied by the client is therefore not trusted
     * for tenant isolation.
     * </p>
     *
     * <p>
     * The method fetches:
     * </p>
     *
     * <ul>
     *     <li>Paginated fee collection records</li>
     *     <li>Total matching record count</li>
     *     <li>Aggregate summary across the complete filtered dataset</li>
     * </ul>
     */
    @Override
    public StudentFeeCollectionReportPageResponse
    getStudentFeeCollectionReport(
            StudentFeeCollectionReportRequest request) {

        /*
         * Get the currently authenticated institute.
         *
         * This is important for multi-tenant data isolation.
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

        Sort sort =
                Sort.by(
                        direction,
                        resolveSortField(
                                request.getSortBy()
                        )
                );

        Pageable pageable =
                PageRequest.of(
                        request.getPage(),
                        request.getSize(),
                        sort
                );

        String feeStatus =
                request.getFeeStatus() != null
                        ? request.getFeeStatus().name()
                        : null;

        /*
         * Fetch paginated report records.
         */
        Page<StudentFeeCollectionReportProjection> page =
                studentFeeCollectionReportRepository
                        .getStudentFeeCollectionReport(
                                instituteId,
                                request.getCourseId(),
                                request.getBatchId(),
                                request.getStudentId(),
                                feeStatus,
                                pageable,
                                request.getSortBy(),
                                request.getSortDirection()
                        );

        /*
         * Convert repository projections into API response DTOs.
         *
         * <p>
         * Repository projections are database/reporting objects.
         * They should not be exposed directly through the API.
         * </p>
         */
        List<StudentFeeCollectionReportResponse> content =
                page.getContent()
                        .stream()
                        .map(projection ->
                                new StudentFeeCollectionReportResponse(
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
                                )
                        )
                        .toList();

        /*
         * Fetch aggregate summary.
         *
         * Summary is calculated independently from pagination,
         * so it represents the complete filtered dataset.
         */
        StudentFeeCollectionReportSummaryResponse summary =
                studentFeeCollectionReportRepository
                        .getSummary(
                                instituteId,
                                request.getCourseId(),
                                request.getBatchId(),
                                request.getStudentId(),
                                feeStatus
                        );

        /*
         * Convert projection records into the response DTO.
         */
        return new StudentFeeCollectionReportPageResponse(
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
     * Resolves the API sort field to a safe entity/report field.
     *
     * <p>
     * Only known fields are accepted from the client.
     * Unknown values fall back to student name.
     * </p>
     */
    private String resolveSortField(String sortBy) {

        if (sortBy == null || sortBy.isBlank()) {
            return "studentName";
        }

        return switch (sortBy) {

            case "studentName" -> "studentName";

            case "courseName" -> "courseName";

            case "batchName" -> "batchName";

            case "totalFee" -> "totalFee";

            case "discount" -> "discount";

            case "finalFee" -> "finalFee";

            case "paidAmount" -> "paidAmount";

            case "pendingAmount" -> "pendingAmount";

            case "feeStatus" -> "feeStatus";

            case "feeDueDate" -> "feeDueDate";

            default -> "studentName";
        };
    }
}