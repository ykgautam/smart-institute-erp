package com.smartinstitute.erp.test.entity;

import com.smartinstitute.erp.common.entity.BaseEntity;
import com.smartinstitute.erp.course.entity.Course;
import com.smartinstitute.erp.institute.entity.Institute;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "topics",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_topic_name_course",
                        columnNames = {
                                "institute_id",
                                "course_id",
                                "name"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Topic extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Integer displayOrder = 1;

    @Column(nullable = false)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "course_id",
            nullable = false
    )
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "institute_id",
            nullable = false
    )
    private Institute institute;

}