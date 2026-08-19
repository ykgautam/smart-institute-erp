package com.smartinstitute.erp.report.dto.request;

import com.smartinstitute.erp.common.enums.fee.FeeStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

/**
 * Request filters for the Student Fee Collection Report.
 *
 * <p>
 * This report uses the current paid amount stored against each
 * student fee record. It does not represent individual payment
 * transactions.
 * </p>
 */
@Getter
@Setter
public class StudentFeeCollectionReportRequest {

    /**
     * Optional course filter.
     */
    private Long courseId;

    /**
     * Optional batch filter.
     */
    private Long batchId;

    /**
     * Optional student filter.
     */
    private Long studentId;

    /**
     * Optional fee-status filter.
     */
    private FeeStatus feeStatus;

    /**
     * Page number requested by the client.
     *
     * <p>
     * Page numbering starts from zero.
     * </p>
     */
    @Min(value = 0, message = "Page number cannot be negative")
    private int page = 0;

    /**
     * Number of records to return on each page.
     */
    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    private int size = 10;

    /**
     * Field by which the report should be sorted.
     *
     * <p>
     * The repository applies a whitelist before using this
     * value in the SQL ORDER BY clause.
     * </p>
     */
    private String sortBy = "pendingAmount";

    /**
     * Sort direction.
     *
     * <p>
     * Supported values are {@code ASC} and {@code DESC}.
     * </p>
     */
    private String sortDirection = "DESC";

}