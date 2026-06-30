package com.smartinstitute.erp.course.entity;

import com.smartinstitute.erp.common.entity.BaseEntity;
import com.smartinstitute.erp.common.enums.CourseStatus;
import com.smartinstitute.erp.common.enums.DurationType;
import com.smartinstitute.erp.institute.entity.Institute;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
        name = "courses",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "institute_id",
                        "course_code"
                }),
                @UniqueConstraint(columnNames = {
                        "institute_id",
                        "course_name"
                })
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Course extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "institute_id",
            nullable = false
    )
    private Institute institute;

    @Column(
            nullable = false,
            length = 30
    )
    private String courseCode;

    @Column(
            nullable = false,
            length = 150
    )
    private String courseName;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Integer duration;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private DurationType durationType;

    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal fee;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private CourseStatus status = CourseStatus.ACTIVE;

    @Column(nullable = false)
    private Boolean active = true;

}