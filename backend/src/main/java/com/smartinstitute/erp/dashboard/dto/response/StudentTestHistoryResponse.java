package com.smartinstitute.erp.dashboard.dto.response;

import com.smartinstitute.erp.common.enums.test.StudentTestStatus;
import lombok.AllArgsConstructor;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentTestHistoryResponse {

    private Long studentTestId;

    private Long testId;

    private String testTitle;

    private BigDecimal percentage;

    private Integer obtainedMarks;

    private Integer totalMarks;

    private Boolean passed;

    private StudentTestStatus status;

    private LocalDateTime startedAt;

    private LocalDateTime submittedAt;

}