package com.smartinstitute.erp.dashboard.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicPerformanceResponse {

    private Long topicId;

    private String topicName;

    private Double averagePercentage;

    private Integer testsAttempted;

}