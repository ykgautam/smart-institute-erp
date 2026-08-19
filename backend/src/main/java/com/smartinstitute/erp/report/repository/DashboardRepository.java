package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.projection.DashboardSummaryProjection;

public interface DashboardRepository {

    DashboardSummaryProjection getDashboardSummary(
            Long instituteId
    );
}