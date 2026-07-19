package com.smartinstitute.erp.dashboard.projection;

public interface CoursePerformanceProjection {

    Long getCourseId();

    String getCourseName();

    Long getTestsAttempted();

    Long getTestsPassed();

    Double getAveragePercentage();

}