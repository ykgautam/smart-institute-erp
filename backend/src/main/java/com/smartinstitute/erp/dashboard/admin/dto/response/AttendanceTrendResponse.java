package com.smartinstitute.erp.dashboard.admin.dto.response;

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
public class AttendanceTrendResponse {

    private Integer year;

    private Integer month;

    private BigDecimal attendancePercentage;
}