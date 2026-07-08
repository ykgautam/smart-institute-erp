package com.smartinstitute.erp.test.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateTopicRequest {

    @NotNull(message = "Course Id is required.")
    private Long courseId;

    @NotBlank(message = "Topic name is required.")
    @Size(max = 150, message = "Topic name cannot exceed 150 characters.")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters.")
    private String description;

    @NotNull(message = "Display order is required.")
    @Min(value = 1, message = "Display order must be at least 1.")
    @Max(value = 9999, message = "Display order cannot exceed 9999.")
    private Integer displayOrder;

    @NotNull(message = "Active status is required.")
    private Boolean active;

}