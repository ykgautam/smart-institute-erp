package com.smartinstitute.erp.batch.entity;

import com.smartinstitute.erp.common.entity.BaseEntity;
import com.smartinstitute.erp.common.enums.BatchStatus;
import com.smartinstitute.erp.course.entity.Course;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(
        name = "batches",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "institute_id",
                                "batch_code"
                        }
                ),
                @UniqueConstraint(
                        columnNames = {
                                "institute_id",
                                "batch_name"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Batch extends BaseEntity {

    @Column(nullable = false, length = 30)
    private String batchCode;

    @Column(nullable = false, length = 100)
    private String batchName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id")
    private User faculty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "institute_id",
            nullable = false
    )
    private Institute institute;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BatchStatus status = BatchStatus.PLANNED;

    @Column(nullable = false)
    private Boolean active = true;

}