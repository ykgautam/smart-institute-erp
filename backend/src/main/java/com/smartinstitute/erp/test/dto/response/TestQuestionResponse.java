package com.smartinstitute.erp.test.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TestQuestionResponse {

    private Long id;

    private Long questionId;

    private String questionText;

    private Integer marks;

    private Integer displayOrder;

}