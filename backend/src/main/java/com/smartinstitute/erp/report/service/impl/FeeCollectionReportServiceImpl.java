package com.smartinstitute.erp.report.service.impl;

import com.smartinstitute.erp.common.exception.BadRequestException;
import com.smartinstitute.erp.common.service.BaseCrudService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.report.dto.request.FeeCollectionReportRequest;
import com.smartinstitute.erp.report.dto.response.FeeCollectionReportPageResponse;
import com.smartinstitute.erp.report.dto.response.FeeCollectionReportResponse;
import com.smartinstitute.erp.report.dto.response.FeeCollectionReportSummaryResponse;
import com.smartinstitute.erp.report.projection.FeeCollectionReportProjection;
import com.smartinstitute.erp.report.projection.FeeCollectionReportSummaryProjection;
import com.smartinstitute.erp.report.repository.FeeCollectionReportRepository;
import com.smartinstitute.erp.report.service.FeeCollectionReportService;
import com.smartinstitute.erp.security.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class FeeCollectionReportServiceImpl
        extends BaseCrudService
        implements FeeCollectionReportService {

    private final FeeCollectionReportRepository
            feeCollectionReportRepository;

    public FeeCollectionReportServiceImpl(
            SecurityUtil securityUtil,
            InstituteAccessValidator instituteAccessValidator,
            FeeCollectionReportRepository feeCollectionReportRepository) {

        super(
                securityUtil,
                instituteAccessValidator
        );

        this.feeCollectionReportRepository =
                feeCollectionReportRepository;
    }

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of(
                    "studentName",
                    "courseName",
                    "batchName",
                    "finalFee",
                    "paidAmount",
                    "pendingAmount",
                    "status"
            );

    private String resolveSortColumn(String sortBy) {

        return switch (sortBy) {

            case "studentName" -> "s.first_name";

            case "courseName" -> "c.course_name";

            case "batchName" -> "b.batch_name";

            case "finalFee" -> "sf.final_fee";

            case "paidAmount" -> "sf.paid_amount";

            case "pendingAmount" -> "sf.pending_amount";

            case "status" -> "sf.status";

            default -> "s.first_name";
        };
    }

    private String resolveSortField(String sortBy) {

        if (sortBy == null || sortBy.isBlank()) {
            return "studentName";
        }

        return switch (sortBy) {

            case "studentName" -> "studentName";

            case "courseName" -> "courseName";

            case "batchName" -> "batchName";

            case "finalFee" -> "finalFee";

            case "paidAmount" -> "paidAmount";

            case "pendingAmount" -> "pendingAmount";

            case "status" -> "status";

            default -> throw new BadRequestException(
                    "Invalid sort field: " + sortBy
            );
        };
    }

    @Override
    public FeeCollectionReportPageResponse getFeeCollectionReport(
            FeeCollectionReportRequest request) {

        validateRequest(request);

        Institute institute = getCurrentInstitute();

        String status = request.getStatus() == null
                ? null
                : request.getStatus().name();

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize()
        );

        Page<FeeCollectionReportProjection> page =
                feeCollectionReportRepository
                        .getFeeCollectionReport(
                                institute.getId(),
                                request.getCourseId(),
                                request.getBatchId(),
                                status,
                                request.getFeeDueDateFrom(),
                                request.getFeeDueDateTo(),
                                pageable
                        );

        FeeCollectionReportSummaryProjection summaryProjection =
                feeCollectionReportRepository
                        .getFeeCollectionReportSummary(
                                institute.getId(),
                                request.getCourseId(),
                                request.getBatchId(),
                                status,
                                request.getFeeDueDateFrom(),
                                request.getFeeDueDateTo()
                        );

        List<FeeCollectionReportResponse> content =
                page.getContent()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        FeeCollectionReportSummaryResponse summary =
                toSummaryResponse(summaryProjection);

        return FeeCollectionReportPageResponse.builder()
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

    private FeeCollectionReportSummaryResponse toSummaryResponse(
            FeeCollectionReportSummaryProjection projection) {

        return FeeCollectionReportSummaryResponse.builder()
                .totalStudents(
                        projection.getTotalStudents() == null
                                ? 0
                                : projection.getTotalStudents()
                )
                .totalFee(
                        projection.getTotalFee() == null
                                ? BigDecimal.ZERO
                                : projection.getTotalFee()
                )
                .totalPaid(
                        projection.getTotalPaid() == null
                                ? BigDecimal.ZERO
                                : projection.getTotalPaid()
                )
                .totalPending(
                        projection.getTotalPending() == null
                                ? BigDecimal.ZERO
                                : projection.getTotalPending()
                )
                .pendingStudents(
                        projection.getPendingStudents() == null
                                ? 0
                                : projection.getPendingStudents()
                )
                .partiallyPaidStudents(
                        projection.getPartiallyPaidStudents() == null
                                ? 0
                                : projection.getPartiallyPaidStudents()
                )
                .paidStudents(
                        projection.getPaidStudents() == null
                                ? 0
                                : projection.getPaidStudents()
                )
                .build();
    }

    private void validateRequest(
            FeeCollectionReportRequest request) {

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

        if (request.getFeeDueDateFrom() != null &&
                request.getFeeDueDateTo() != null &&
                request.getFeeDueDateFrom()
                        .isAfter(request.getFeeDueDateTo())) {

            throw new BadRequestException(
                    "Fee due date from must not be after fee due date to."
            );
        }
    }

    private FeeCollectionReportResponse toResponse(
            FeeCollectionReportProjection projection) {

        return FeeCollectionReportResponse.builder()
                .studentId(projection.getStudentId())
                .studentName(projection.getStudentName())
                .courseName(projection.getCourseName())
                .batchName(projection.getBatchName())
                .finalFee(projection.getFinalFee())
                .paidAmount(projection.getPaidAmount())
                .pendingAmount(projection.getPendingAmount())
                .status(projection.getStatus())
                .build();
    }
}