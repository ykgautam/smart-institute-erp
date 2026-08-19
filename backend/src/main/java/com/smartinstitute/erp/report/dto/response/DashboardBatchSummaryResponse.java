package com.smartinstitute.erp.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Batch-related KPI information displayed on the admin dashboard.
 */
@Getter
@AllArgsConstructor
public class DashboardBatchSummaryResponse {

    private Long totalBatches;

    private Long activeBatches;
}