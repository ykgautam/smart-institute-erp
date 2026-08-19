package com.smartinstitute.erp.report.service;

import com.smartinstitute.erp.report.dto.response.DashboardResponse;

public interface DashboardService {

    /**
     * Fetches high-level dashboard information for the
     * currently authenticated institute.
     *
     * @return dashboard KPI information
     */
    DashboardResponse getDashboard();
}