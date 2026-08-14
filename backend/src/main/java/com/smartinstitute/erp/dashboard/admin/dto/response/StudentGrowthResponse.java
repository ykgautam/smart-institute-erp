package com.smartinstitute.erp.dashboard.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentGrowthResponse {

    private Integer year;

    private Integer month;

    private Long studentCount;
}