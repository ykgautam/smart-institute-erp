package com.smartinstitute.erp.test.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTestQuestionOrderRequest {

    @NotNull
    private Long questionId;

    @NotNull
    @Min(1)
    private Integer displayOrder;

}