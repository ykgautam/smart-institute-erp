package com.smartinstitute.erp.dashboard.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyPerformanceResponse {

    private Integer year;

    private Integer month;

    private Double averagePercentage;

    private Integer testsAttempted;

}