package com.smartinstitute.erp.test.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartTestRequest {

    @NotNull
    private Long testId;

}