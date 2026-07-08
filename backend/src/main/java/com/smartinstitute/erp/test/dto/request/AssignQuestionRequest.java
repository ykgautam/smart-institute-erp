package com.smartinstitute.erp.test.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssignQuestionRequest {

    @NotEmpty
    @Valid
    private List<TestQuestionRequest> questions;

}