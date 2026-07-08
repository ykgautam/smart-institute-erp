package com.smartinstitute.erp.test.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddQuestionToTestRequest {

    @NotEmpty
    private List<@NotNull Long> questionIds;

}