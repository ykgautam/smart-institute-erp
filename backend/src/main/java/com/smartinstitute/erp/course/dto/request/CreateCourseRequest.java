package com.smartinstitute.erp.course.dto.request;

import com.smartinstitute.erp.common.enums.DurationType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateCourseRequest {

    @NotBlank(message = "Course code is required.")
    @Size(max = 30)
    private String courseCode;

    @NotBlank(message = "Course name is required.")
    @Size(max = 150)
    private String courseName;

    @Size(max = 500)
    private String description;

    @NotNull(message = "Duration is required.")
    @Positive(message = "Duration must be greater than zero.")
    private Integer duration;

    @NotNull(message = "Duration type is required.")
    private DurationType durationType;

    @NotNull(message = "Course fee is required.")
    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Fee cannot be negative."
    )
    @Digits(
            integer = 10,
            fraction = 2
    )
    private BigDecimal fee;

}