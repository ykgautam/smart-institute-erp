package com.smartinstitute.erp.dashboard.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResultResponse {

    private Long questionId;

    private String question;

    private String selectedOption;

    private String correctOption;

    private Boolean correct;

    private Integer marksObtained;

}