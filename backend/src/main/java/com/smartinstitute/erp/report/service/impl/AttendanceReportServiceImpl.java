package com.smartinstitute.erp.report.service.impl;

import com.smartinstitute.erp.common.exception.BadRequestException;
import com.smartinstitute.erp.common.service.BaseCrudService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.report.dto.request.AttendanceReportRequest;
import com.smartinstitute.erp.report.dto.response.AttendanceReportPageResponse;
import com.smartinstitute.erp.report.dto.response.AttendanceReportResponse;
import com.smartinstitute.erp.report.dto.response.AttendanceReportSummaryResponse;
import com.smartinstitute.erp.report.projection.AttendanceReportProjection;
import com.smartinstitute.erp.report.projection.AttendanceReportSummaryProjection;
import com.smartinstitute.erp.report.repository.AttendanceReportRepository;
import com.smartinstitute.erp.report.service.AttendanceReportService;
import com.smartinstitute.erp.security.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AttendanceReportServiceImpl
        extends BaseCrudService
        implements AttendanceReportService {

    private final AttendanceReportRepository
            attendanceReportRepository;

    public AttendanceReportServiceImpl(
            SecurityUtil securityUtil,
            InstituteAccessValidator instituteAccessValidator,
            AttendanceReportRepository attendanceReportRepository) {

        super(
                securityUtil,
                instituteAccessValidator
        );

        this.attendanceReportRepository =
                attendanceReportRepository;
    }

    @Override
    public AttendanceReportPageResponse getAttendanceReport(
            AttendanceReportRequest request) {

        validateRequest(request);

        Institute institute = getCurrentInstitute();

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize()
        );

        Page<AttendanceReportProjection> page =
                attendanceReportRepository.getAttendanceReport(
                        institute.getId(),
                        request.getCourseId(),
                        request.getBatchId(),
                        request.getAttendanceDateFrom(),
                        request.getAttendanceDateTo(),
                        pageable
                );

        List<AttendanceReportResponse> content =
                page.getContent()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        AttendanceReportSummaryProjection summaryProjection =
                attendanceReportRepository
                        .getAttendanceReportSummary(
                                institute.getId(),
                                request.getCourseId(),
                                request.getBatchId(),
                                request.getAttendanceDateFrom(),
                                request.getAttendanceDateTo()
                        );
        AttendanceReportSummaryResponse summary =
                toSummaryResponse(summaryProjection);

        return AttendanceReportPageResponse.builder()
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

    private AttendanceReportSummaryResponse toSummaryResponse(
            AttendanceReportSummaryProjection projection) {

        if (projection == null) {
            return AttendanceReportSummaryResponse.builder()
                    .totalStudents(0)
                    .totalClasses(0)
                    .totalPresent(0)
                    .totalAbsent(0)
                    .averageAttendancePercentage(BigDecimal.ZERO)
                    .lowAttendanceStudents(0)
                    .build();
        }

        return AttendanceReportSummaryResponse.builder()
                .totalStudents(
                        projection.getTotalStudents() == null
                                ? 0
                                : projection.getTotalStudents()
                )
                .totalClasses(
                        projection.getTotalClasses() == null
                                ? 0
                                : projection.getTotalClasses()
                )
                .totalPresent(
                        projection.getTotalPresent() == null
                                ? 0
                                : projection.getTotalPresent()
                )
                .totalAbsent(
                        projection.getTotalAbsent() == null
                                ? 0
                                : projection.getTotalAbsent()
                )
                .averageAttendancePercentage(
                        projection.getAverageAttendancePercentage() == null
                                ? BigDecimal.ZERO
                                : projection.getAverageAttendancePercentage()
                )
                .lowAttendanceStudents(
                        projection.getLowAttendanceStudents() == null
                                ? 0
                                : projection.getLowAttendanceStudents()
                )
                .build();
    }
    
    private void validateRequest(
            AttendanceReportRequest request) {

        if (request == null) {
            throw new BadRequestException(
                    "Attendance report request must not be null."
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

        if (request.getAttendanceDateFrom() != null &&
                request.getAttendanceDateTo() != null &&
                request.getAttendanceDateFrom()
                        .isAfter(request.getAttendanceDateTo())) {

            throw new BadRequestException(
                    "Attendance date from must not be after attendance date to."
            );
        }
    }

    private AttendanceReportResponse toResponse(
            AttendanceReportProjection projection) {

        return AttendanceReportResponse.builder()
                .studentId(projection.getStudentId())
                .studentName(projection.getStudentName())
                .courseName(projection.getCourseName())
                .batchName(projection.getBatchName())
                .totalClasses(
                        projection.getTotalClasses() == null
                                ? 0
                                : projection.getTotalClasses()
                )
                .presentClasses(
                        projection.getPresentClasses() == null
                                ? 0
                                : projection.getPresentClasses()
                )
                .absentClasses(
                        projection.getAbsentClasses() == null
                                ? 0
                                : projection.getAbsentClasses()
                )
                .attendancePercentage(
                        projection.getAttendancePercentage() == null
                                ? BigDecimal.ZERO
                                : projection.getAttendancePercentage()
                )
                .build();
    }
}