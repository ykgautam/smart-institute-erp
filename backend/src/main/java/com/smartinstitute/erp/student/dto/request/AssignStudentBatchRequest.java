package com.smartinstitute.erp.student.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignStudentBatchRequest {

    @NotNull(message = "Batch ID is required.")
    private Long batchId;

}