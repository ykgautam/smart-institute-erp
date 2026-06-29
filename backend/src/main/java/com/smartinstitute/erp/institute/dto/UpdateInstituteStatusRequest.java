package com.smartinstitute.erp.institute.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateInstituteStatusRequest {

    @NotNull(message = "Active status is required.")
    private Boolean active;

}