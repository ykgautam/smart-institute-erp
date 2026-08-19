package com.smartinstitute.erp.report.dto.response;

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
public class StudentReportResponse {

    private Long studentId;

    private String studentName;

    private String email;

    private String courseName;

    private String batchName;

    private String status;
}