package com.smartinstitute.erp.student.dto;

import com.smartinstitute.erp.common.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateStudentRequest {

    @Size(max = 30)
    private String rollNumber;

    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    private Gender gender;

    @Past
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^[0-9]{10}$")
    private String mobile;

    @Email
    @Size(max = 150)
    private String email;

    @Size(max = 150)
    private String fatherName;

    @Size(max = 150)
    private String motherName;

    @Pattern(regexp = "^[0-9]{10}$")
    private String guardianMobile;

    @Size(max = 300)
    private String address;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String state;

    @Pattern(regexp = "^[0-9]{6}$")
    private String pincode;

    private LocalDate admissionDate;

}