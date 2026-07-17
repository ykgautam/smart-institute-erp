package com.smartinstitute.erp.test.dto.response;

import com.smartinstitute.erp.common.enums.test.QuestionDifficulty;
import com.smartinstitute.erp.common.enums.test.QuestionType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class QuestionForStudentResponse {

    private Long id;

    private String questionText;

    private QuestionType questionType;

    private QuestionDifficulty difficulty;

    private Integer marks;

    private List<QuestionOptionForStudentResponse> options;

}