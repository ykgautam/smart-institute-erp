package com.smartinstitute.erp.report.projection;

import java.math.BigDecimal;

public interface FeeCollectionReportSummaryProjection {

    Long getTotalStudents();

    BigDecimal getTotalFee();

    BigDecimal getTotalPaid();

    BigDecimal getTotalPending();

    Long getPendingStudents();

    Long getPartiallyPaidStudents();

    Long getPaidStudents();
}