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
public class QuestionResponse {

    private Long id;

    private Long courseId;

    private String courseName;

    private Long topicId;

    private String topicName;

    private String questionText;

    private QuestionType questionType;

    private QuestionDifficulty difficulty;

    private String explanation;

    private Integer marks;

    private List<QuestionOptionResponse> options;

}