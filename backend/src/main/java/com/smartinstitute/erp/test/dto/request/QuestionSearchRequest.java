package com.smartinstitute.erp.test.dto.request;

import com.smartinstitute.erp.common.enums.test.QuestionDifficulty;
import com.smartinstitute.erp.common.enums.test.QuestionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionSearchRequest {

    private Long courseId;

    private Long topicId;

    private QuestionDifficulty difficulty;

    private QuestionType questionType;

    private String keyword;

}