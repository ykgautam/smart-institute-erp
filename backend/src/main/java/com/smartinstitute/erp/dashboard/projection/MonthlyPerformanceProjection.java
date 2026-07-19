package com.smartinstitute.erp.dashboard.projection;

public interface MonthlyPerformanceProjection {

    Integer getYear();

    Integer getMonth();

    Double getAveragePercentage();

    Long getTestsAttempted();

}