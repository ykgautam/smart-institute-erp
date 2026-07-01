package com.smartinstitute.erp.batch.dto.response;

import com.smartinstitute.erp.common.enums.BatchStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchResponse {

    private Long id;

    private String batchCode;

    private String batchName;

    private Long courseId;

    private String courseName;

    private Long facultyId;

    private String facultyName;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private Integer capacity;

    private BatchStatus status;

    private Boolean active;

    private Integer studentCount;

}