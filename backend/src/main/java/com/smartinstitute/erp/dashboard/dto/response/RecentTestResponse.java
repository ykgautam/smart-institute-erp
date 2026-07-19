package com.smartinstitute.erp.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentTestResponse {

    private Long studentTestId;

    private Long testId;

    private String testTitle;

    private BigDecimal percentage;

    private Boolean passed;

    private LocalDateTime submittedAt;

}