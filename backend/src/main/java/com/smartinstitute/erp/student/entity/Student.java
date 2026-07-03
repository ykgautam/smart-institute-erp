package com.smartinstitute.erp.student.entity;

import com.smartinstitute.erp.attendance.entity.Attendance;
import com.smartinstitute.erp.batch.entity.Batch;
import com.smartinstitute.erp.common.entity.BaseEntity;
import com.smartinstitute.erp.common.enums.Gender;
import com.smartinstitute.erp.common.enums.StudentStatus;
import com.smartinstitute.erp.institute.entity.Institute;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(
        name = "students",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "institute_id",
                        "admission_number"
                }),
                @UniqueConstraint(columnNames = {
                        "institute_id",
                        "mobile"
                }),
                @UniqueConstraint(columnNames = {
                        "institute_id",
                        "email"
                })
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Student extends BaseEntity {

    @Column(nullable = false, length = 30)
    private String admissionNumber;

    @Column(length = 30)
    private String rollNumber;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    private LocalDate dateOfBirth;

    @Column(length = 20)
    private String mobile;

    @Column(length = 150)
    private String email;

    @Column(nullable = false, length = 150)
    private String fatherName;

    @Column(length = 150)
    private String motherName;

    @Column(length = 20)
    private String guardianMobile;

    @Column(length = 300)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 10)
    private String pincode;

    @Column(length = 300)
    private String photoPath;

    private LocalDate admissionDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentStatus status = StudentStatus.ACTIVE;

    @Column(nullable = false)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "institute_id",
            nullable = false
    )
    private Institute institute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private Batch batch;

    @OneToMany(mappedBy = "student")
    private List<Attendance> attendances;
}