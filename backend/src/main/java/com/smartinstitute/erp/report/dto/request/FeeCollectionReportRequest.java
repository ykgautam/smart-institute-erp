package com.smartinstitute.erp.report.dto.request;

import com.smartinstitute.erp.common.enums.fee.FeeStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FeeCollectionReportRequest {

    private Long courseId;

    private Long batchId;

    private FeeStatus status;

    private LocalDate feeDueDateFrom;

    private LocalDate feeDueDateTo;

    @Min(0)
    private int page = 0;

    @Min(1)
    @Max(100)
    private int size = 10;

    private String sortBy = "studentName";

    private String sortDirection = "asc";
}