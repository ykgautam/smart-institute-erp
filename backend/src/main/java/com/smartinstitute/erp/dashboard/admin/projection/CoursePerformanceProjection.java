package com.smartinstitute.erp.dashboard.admin.projection;

import java.math.BigDecimal;

public interface CoursePerformanceProjection {

    Long getCourseId();

    String getCourseName();

    Long getTestsAttempted();

    Long getTestsPassed();

    BigDecimal getAveragePercentage();
}