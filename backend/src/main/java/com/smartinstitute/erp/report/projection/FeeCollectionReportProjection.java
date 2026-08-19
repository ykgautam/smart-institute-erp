package com.smartinstitute.erp.report.projection;

import java.math.BigDecimal;

public interface FeeCollectionReportProjection {

    Long getStudentId();

    String getStudentName();

    String getCourseName();

    String getBatchName();

    BigDecimal getFinalFee();

    BigDecimal getPaidAmount();

    BigDecimal getPendingAmount();

    String getStatus();
}