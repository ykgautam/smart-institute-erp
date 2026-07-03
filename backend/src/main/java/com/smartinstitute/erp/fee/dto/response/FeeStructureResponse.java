package com.smartinstitute.erp.fee.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FeeStructureResponse {

    private Long id;

    private Long courseId;

    private String courseName;

    private Double amount;

    private String description;

    private Boolean active;

}