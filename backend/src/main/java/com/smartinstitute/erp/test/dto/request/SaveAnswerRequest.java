package com.smartinstitute.erp.test.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveAnswerRequest {

    @NotNull
    private Long questionId;

    /**
     * Null means student cleared/skipped the answer.
     */
    private Long selectedOptionId;

}