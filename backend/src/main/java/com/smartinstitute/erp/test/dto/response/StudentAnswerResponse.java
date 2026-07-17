package com.smartinstitute.erp.test.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StudentAnswerResponse {

    private Long questionId;

    private Long selectedOptionId;

}