package com.smartinstitute.erp.dashboard.projection;

public interface TopicPerformanceProjection {

    Long getTopicId();

    String getTopicName();

    Double getAveragePercentage();

    Long getTestsAttempted();

}