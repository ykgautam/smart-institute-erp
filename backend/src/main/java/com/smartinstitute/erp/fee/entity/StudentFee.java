package com.smartinstitute.erp.fee.entity;

import com.smartinstitute.erp.common.entity.BaseEntity;
import com.smartinstitute.erp.common.enums.fee.FeeStatus;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.student.entity.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "student_fees",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_fee_student",
                        columnNames = "student_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentFee extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "student_id",
            nullable = false
    )
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "fee_structure_id",
            nullable = false
    )
    private FeeStructure feeStructure;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "institute_id",
            nullable = false
    )
    private Institute institute;

    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal totalFee;

    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal finalFee;

    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal pendingAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeeStatus status;

    @Column(nullable = false)
    private Boolean active = true;

    private LocalDate feeDueDate;

}