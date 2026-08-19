package com.smartinstitute.erp.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeCollectionReportSummaryResponse {

    private long totalStudents;

    private BigDecimal totalFee;

    private BigDecimal totalPaid;

    private BigDecimal totalPending;

    private long pendingStudents;

    private long partiallyPaidStudents;

    private long paidStudents;
}