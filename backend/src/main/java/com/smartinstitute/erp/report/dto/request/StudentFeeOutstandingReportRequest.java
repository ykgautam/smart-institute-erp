package com.smartinstitute.erp.report.dto.request;

import com.smartinstitute.erp.common.enums.fee.FeeStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Request filters for the Student Fee Outstanding Report.
 *
 * <p>
 * This DTO is used by administrators to filter and paginate
 * outstanding student fee records.
 * </p>
 *
 * <p>
 * The instituteId is intentionally not trusted from the client.
 * The service layer obtains the current institute from the
 * authenticated user context.
 * </p>
 */
@Getter
@Setter
public class StudentFeeOutstandingReportRequest {

    /**
     * Institute identifier.
     *
     * <p>
     * This field is retained for request compatibility, but the
     * service layer should use the authenticated user's institute
     * instead of trusting this value.
     * </p>
     */
    private Long instituteId;

    /**
     * Optional course filter.
     */
    private Long courseId;

    /**
     * Optional batch filter.
     */
    private Long batchId;

    /**
     * Optional fee status filter.
     */
    private FeeStatus feeStatus;

    /**
     * Optional minimum fee due date.
     */
    private LocalDate dueDateFrom;

    /**
     * Optional maximum fee due date.
     */
    private LocalDate dueDateTo;

    /**
     * Page number.
     *
     * <p>
     * Zero-based page index.
     * </p>
     */
    private Integer page = 0;

    /**
     * Number of records per page.
     */
    private Integer size = 10;

    /**
     * Field used for sorting the report.
     */
    private String sortBy = "pendingAmount";

    /**
     * Sort direction.
     *
     * <p>
     * Supported values are ASC and DESC.
     * </p>
     */
    private String sortDirection = "DESC";
}