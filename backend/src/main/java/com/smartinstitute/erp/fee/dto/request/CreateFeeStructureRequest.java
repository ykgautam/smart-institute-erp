package com.smartinstitute.erp.fee.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFeeStructureRequest {

    @NotNull
    private Long courseId;

    @NotNull
    @Positive
    private Double amount;

    @Size(max = 300)
    private String description;

}