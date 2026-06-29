package com.smartinstitute.erp.student.dto;

import com.smartinstitute.erp.common.enums.StudentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentStatusRequest {

    @NotNull(message = "Student status is required.")
    private StudentStatus status;

}