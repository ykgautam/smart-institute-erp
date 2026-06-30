package com.smartinstitute.erp.course.dto.response;

import com.smartinstitute.erp.common.enums.CourseStatus;
import com.smartinstitute.erp.common.enums.DurationType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CourseResponse {

    private Long id;

    private String courseCode;

    private String courseName;

    private String description;

    private Integer duration;

    private DurationType durationType;

    private BigDecimal fee;

    private CourseStatus status;

    private Boolean active;

}