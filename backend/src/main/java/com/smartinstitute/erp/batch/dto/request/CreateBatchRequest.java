package com.smartinstitute.erp.batch.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class CreateBatchRequest {

    @NotBlank(message = "Batch code is required.")
    private String batchCode;

    @NotBlank(message = "Batch name is required.")
    private String batchName;

    @NotNull(message = "Course is required.")
    private Long courseId;

    private Long facultyId;

    @NotNull(message = "Start date is required.")
    private LocalDate startDate;

    @NotNull(message = "End date is required.")
    private LocalDate endDate;

    @NotNull(message = "Start time is required.")
    private LocalTime startTime;

    @NotNull(message = "End time is required.")
    private LocalTime endTime;

    @NotNull(message = "Capacity is required.")
    @Min(value = 1, message = "Capacity must be greater than zero.")
    private Integer capacity;

}