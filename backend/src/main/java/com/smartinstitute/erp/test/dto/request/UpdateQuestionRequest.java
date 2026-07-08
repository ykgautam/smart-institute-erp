package com.smartinstitute.erp.test.dto.request;

import com.smartinstitute.erp.common.enums.test.QuestionDifficulty;
import com.smartinstitute.erp.common.enums.test.QuestionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateQuestionRequest {

    @NotNull
    private Long courseId;

    @NotNull
    private Long topicId;

    @NotBlank
    @Size(max = 2000)
    private String questionText;

    @NotNull
    private QuestionType questionType;

    @NotNull
    private QuestionDifficulty difficulty;

    @Size(max = 3000)
    private String explanation;

    @NotNull
    @Positive
    private Integer marks;

    @NotEmpty
    @Size(min = 2, max = 6)
    @Valid
    private List<QuestionOptionRequest> options;

}