package com.smartinstitute.erp.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Paginated response for the Student Fee Collection Report.
 *
 * <p>
 * Contains both the individual collection records and an aggregate
 * summary for the complete filtered dataset.
 * </p>
 */
@Getter
@AllArgsConstructor
public class StudentFeeCollectionReportPageResponse {

    private List<StudentFeeCollectionReportResponse> content;

    private StudentFeeCollectionReportSummaryResponse summary;

    private long totalElements;

    private int totalPages;

    private int currentPage;

    private int pageSize;

    private boolean first;

    private boolean last;
}