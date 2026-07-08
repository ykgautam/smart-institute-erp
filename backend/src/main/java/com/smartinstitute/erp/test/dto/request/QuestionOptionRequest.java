package com.smartinstitute.erp.test.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionOptionRequest {

    @NotBlank
    @Size(max = 1000)
    private String optionText;

    @NotNull
    private Boolean correct;

    @NotNull
    private Integer displayOrder;

}