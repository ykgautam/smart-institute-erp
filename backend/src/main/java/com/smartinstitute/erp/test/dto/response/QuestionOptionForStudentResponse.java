package com.smartinstitute.erp.test.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class QuestionOptionForStudentResponse {

    private Long id;

    private String optionText;

}