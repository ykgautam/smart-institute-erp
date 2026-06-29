package com.smartinstitute.erp.student.dto;

import com.smartinstitute.erp.common.enums.Gender;
import com.smartinstitute.erp.common.enums.StudentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class StudentResponse {

    private Long id;

    private String admissionNumber;

    private String rollNumber;

    private String firstName;

    private String lastName;

    private String fullName;

    private Gender gender;

    private LocalDate dateOfBirth;

    private String mobile;

    private String email;

    private String fatherName;

    private String motherName;

    private String guardianMobile;

    private String address;

    private String city;

    private String state;

    private String pincode;

    private String photoPath;

    private LocalDate admissionDate;

    private StudentStatus status;

    private Boolean active;

}