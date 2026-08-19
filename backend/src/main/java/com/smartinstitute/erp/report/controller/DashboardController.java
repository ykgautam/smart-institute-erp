package com.smartinstitute.erp.report.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.report.dto.response.DashboardResponse;
import com.smartinstitute.erp.report.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for the Admin Dashboard.
 *
 * <p>
 * Provides high-level institute KPIs required by the admin dashboard.
 * The dashboard data is resolved for the currently authenticated institute.
 * </p>
 *
 * <p>
 * The controller intentionally does not accept an institute ID from the
 * client. Institute resolution is handled by the service layer through
 * the authenticated user context and {@code BaseCrudService}.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/admin/dashboardDetails")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Creates the DashboardController with its required service dependency.
     *
     * @param dashboardService service responsible for fetching dashboard data
     */
    public DashboardController(
            DashboardService dashboardService) {

        this.dashboardService =
                dashboardService;
    }

    /**
     * Fetches dashboard information for the currently authenticated institute.
     *
     * <p>
     * The request does not require an institute ID because the institute is
     * determined from the authenticated user's security context.
     * </p>
     *
     * @return dashboard KPI information wrapped inside the standard API response
     */
    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>>
    getDashboard() {

        /*
         * Delegate dashboard data retrieval to the service layer.
         *
         * The service is responsible for resolving the current institute
         * and retrieving the corresponding dashboard metrics.
         */
        DashboardResponse response =
                dashboardService.getDashboard();

        /*
         * Wrap the dashboard response using the project's
         * standard ApiResponse structure.
         */
        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        response,
                        "Dashboard data fetched successfully."
                )
        );
    }
}