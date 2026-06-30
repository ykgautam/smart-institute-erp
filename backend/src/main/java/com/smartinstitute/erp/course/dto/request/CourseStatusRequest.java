package com.smartinstitute.erp.course.dto.request;

import com.smartinstitute.erp.common.enums.CourseStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseStatusRequest {

    @NotNull(message = "Course status is required.")
    private CourseStatus status;

}