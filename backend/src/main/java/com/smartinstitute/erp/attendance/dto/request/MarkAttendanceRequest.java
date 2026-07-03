package com.smartinstitute.erp.attendance.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class MarkAttendanceRequest {

    @NotNull(message = "Batch Id is required.")
    private Long batchId;

    @NotNull(message = "Attendance date is required.")
    private LocalDate attendanceDate;

    @Valid
    @NotEmpty(message = "Attendance list cannot be empty.")
    private List<AttendanceEntryRequest> attendanceList;

}