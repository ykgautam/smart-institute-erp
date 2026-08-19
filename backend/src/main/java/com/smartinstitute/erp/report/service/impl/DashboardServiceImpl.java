package com.smartinstitute.erp.report.service.impl;

import com.smartinstitute.erp.common.service.BaseCrudService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.report.dto.response.DashboardBatchSummaryResponse;
import com.smartinstitute.erp.report.dto.response.DashboardCourseSummaryResponse;
import com.smartinstitute.erp.report.dto.response.DashboardResponse;
import com.smartinstitute.erp.report.dto.response.DashboardStudentSummaryResponse;
import com.smartinstitute.erp.report.projection.DashboardSummaryProjection;
import com.smartinstitute.erp.report.repository.DashboardRepository;
import com.smartinstitute.erp.report.service.DashboardService;
import com.smartinstitute.erp.security.util.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl
        extends BaseCrudService
        implements DashboardService {

    private final DashboardRepository dashboardRepository;

    public DashboardServiceImpl(
            SecurityUtil securityUtil,
            InstituteAccessValidator instituteAccessValidator,
            DashboardRepository dashboardRepository) {

        super(
                securityUtil,
                instituteAccessValidator
        );

        this.dashboardRepository =
                dashboardRepository;
    }

    @Override
    public DashboardResponse getDashboard() {

        /*
         * Resolve the institute from the authenticated
         * user/security context.
         *
         * The institute ID is intentionally NOT accepted
         * from the API request.
         */
        Institute institute =
                getCurrentInstitute();

        Long instituteId =
                institute.getId();

        /*
         * Fetch all dashboard KPIs using a single
         * repository call.
         */
        DashboardSummaryProjection projection =
                dashboardRepository
                        .getDashboardSummary(instituteId);

        /*
         * Convert repository projection into
         * API response DTOs.
         */
        DashboardStudentSummaryResponse students =
                new DashboardStudentSummaryResponse(
                        projection.getTotalStudents(),
                        projection.getActiveStudents()
                );

        DashboardCourseSummaryResponse courses =
                new DashboardCourseSummaryResponse(
                        projection.getTotalCourses(),
                        projection.getActiveCourses()
                );

        DashboardBatchSummaryResponse batches =
                new DashboardBatchSummaryResponse(
                        projection.getTotalBatches(),
                        projection.getActiveBatches()
                );

        /*
         * Build the final dashboard response.
         */
        return new DashboardResponse(
                students,
                courses,
                batches
        );
    }
}