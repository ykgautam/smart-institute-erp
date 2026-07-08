package com.smartinstitute.erp.test.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class QuestionOptionResponse {

    private Long id;

    private String optionText;

    private Boolean correct;

    private Integer displayOrder;

}