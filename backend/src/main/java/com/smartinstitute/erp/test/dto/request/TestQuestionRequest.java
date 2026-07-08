package com.smartinstitute.erp.test.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestQuestionRequest {

    @NotNull
    private Long questionId;

    @NotNull
    @Positive
    private Integer displayOrder;

}