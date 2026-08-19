package com.smartinstitute.erp.report.service.impl;

import com.smartinstitute.erp.common.service.BaseCrudService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.report.dto.request.BatchPerformanceReportRequest;
import com.smartinstitute.erp.report.dto.response.BatchPerformanceReportPageResponse;
import com.smartinstitute.erp.report.dto.response.BatchPerformanceReportResponse;
import com.smartinstitute.erp.report.projection.BatchPerformanceReportProjection;
import com.smartinstitute.erp.report.repository.BatchPerformanceReportRepository;
import com.smartinstitute.erp.report.service.BatchPerformanceReportService;
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
public class BatchPerformanceReportServiceImpl
        extends BaseCrudService
        implements BatchPerformanceReportService {

    private final BatchPerformanceReportRepository
            batchPerformanceReportRepository;

    public BatchPerformanceReportServiceImpl(
            SecurityUtil securityUtil,
            InstituteAccessValidator instituteAccessValidator,
            BatchPerformanceReportRepository batchPerformanceReportRepository) {
        super(
                securityUtil,
                instituteAccessValidator
        );

        this.batchPerformanceReportRepository =
                batchPerformanceReportRepository;
    }

    @Override
    public BatchPerformanceReportPageResponse getBatchPerformanceReport(
            BatchPerformanceReportRequest request) {

        Pageable pageable = buildPageable(request);

        Institute institute = getCurrentInstitute();

        Page<BatchPerformanceReportProjection> page =
                batchPerformanceReportRepository
                        .getBatchPerformanceReportWithSorting(
                                institute.getId(),
                                request.getCourseId(),
                                request.getBatchId(),
                                request.getSubmittedFrom(),
                                request.getSubmittedTo(),
                                pageable,
                                request.getSortBy(),
                                request.getSortDirection()
                        );

        List<BatchPerformanceReportResponse> content =
                page.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return new BatchPerformanceReportPageResponse(
                content,
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize(),
                page.isFirst(),
                page.isLast()
        );
    }

    private Pageable buildPageable(
            BatchPerformanceReportRequest request) {

        Sort.Direction direction =
                "DESC".equalsIgnoreCase(request.getSortDirection())
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        String sortBy =
                resolveSortColumn(request.getSortBy());

        return PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(direction, sortBy)
        );
    }

    private String resolveSortColumn(String sortBy) {

        if (sortBy == null || sortBy.isBlank()) {
            return "batchName";
        }

        return switch (sortBy) {

            case "batchName" -> "batchName";

            case "courseName" -> "courseName";

            case "totalStudents" -> "totalStudents";

            case "studentsAttempted" -> "studentsAttempted";

            case "totalAttempts" -> "totalAttempts";

            case "passedAttempts" -> "passedAttempts";

            case "failedAttempts" -> "failedAttempts";

            case "averagePercentage" -> "averagePercentage";

            default -> "batchName";
        };
    }

    private BatchPerformanceReportResponse mapToResponse(
            BatchPerformanceReportProjection projection) {

        return new BatchPerformanceReportResponse(
                projection.getBatchId(),
                projection.getBatchName(),
                projection.getCourseName(),
                projection.getTotalStudents(),
                projection.getTotalAttempts(),
                projection.getSubmittedAttempts(),
                projection.getPassedAttempts(),
                projection.getFailedAttempts(),
                projection.getAveragePercentage(),
                projection.getStudentsAttempted()
        );
    }
}