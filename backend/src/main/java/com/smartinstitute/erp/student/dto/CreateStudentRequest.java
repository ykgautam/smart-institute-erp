package com.smartinstitute.erp.student.dto;

import com.smartinstitute.erp.common.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateStudentRequest {

    @NotBlank(message = "Admission number is required.")
    @Size(max = 30)
    private String admissionNumber;

    @Size(max = 30)
    private String rollNumber;

    @NotBlank(message = "First name is required.")
    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    private Gender gender;

    @Past(message = "Date of birth must be in the past.")
    private LocalDate dateOfBirth;

    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Mobile number must contain exactly 10 digits."
    )
    private String mobile;

    @Email(message = "Invalid email format.")
    @Size(max = 150)
    private String email;

    @NotBlank(message = "Father name is required.")
    @Size(max = 150)
    private String fatherName;

    @Size(max = 150)
    private String motherName;

    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Guardian mobile must contain exactly 10 digits."
    )
    private String guardianMobile;

    @Size(max = 300)
    private String address;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String state;

    @Pattern(
            regexp = "^[0-9]{6}$",
            message = "Pincode must contain exactly 6 digits."
    )
    private String pincode;

    private LocalDate admissionDate;

}