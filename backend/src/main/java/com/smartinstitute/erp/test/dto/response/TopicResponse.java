package com.smartinstitute.erp.test.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TopicResponse {

    private Long id;

    private String name;

    private String description;

    private Integer displayOrder;

    private Boolean active;

    private Long courseId;

    private String courseName;

    private String courseCode;

    private Long instituteId;

}