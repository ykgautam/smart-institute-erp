package com.smartinstitute.erp.test.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionStatusRequest {

    @NotNull
    private Boolean active;

}