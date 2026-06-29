package com.smartinstitute.erp.student.mapper;

import com.smartinstitute.erp.common.enums.StudentStatus;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.student.dto.CreateStudentRequest;
import com.smartinstitute.erp.student.dto.StudentResponse;
import com.smartinstitute.erp.student.dto.UpdateStudentRequest;
import com.smartinstitute.erp.student.entity.Student;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StudentMapper {

    public Student toEntity(
            CreateStudentRequest request,
            Institute institute) {

        Student student = new Student();

        student.setAdmissionNumber(request.getAdmissionNumber());
        student.setRollNumber(request.getRollNumber());

        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());

        student.setGender(request.getGender());
        student.setDateOfBirth(request.getDateOfBirth());

        student.setMobile(request.getMobile());
        student.setEmail(request.getEmail());

        student.setFatherName(request.getFatherName());
        student.setMotherName(request.getMotherName());

        student.setGuardianMobile(request.getGuardianMobile());

        student.setAddress(request.getAddress());
        student.setCity(request.getCity());
        student.setState(request.getState());
        student.setPincode(request.getPincode());

        student.setAdmissionDate(request.getAdmissionDate());

        student.setInstitute(institute);

        student.setStatus(StudentStatus.ACTIVE);

        student.setActive(true);

        return student;
    }

    public void updateEntity(
            Student student,
            UpdateStudentRequest request) {

        student.setRollNumber(request.getRollNumber());

        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());

        student.setGender(request.getGender());
        student.setDateOfBirth(request.getDateOfBirth());

        student.setMobile(request.getMobile());
        student.setEmail(request.getEmail());

        student.setFatherName(request.getFatherName());
        student.setMotherName(request.getMotherName());

        student.setGuardianMobile(request.getGuardianMobile());

        student.setAddress(request.getAddress());
        student.setCity(request.getCity());
        student.setState(request.getState());
        student.setPincode(request.getPincode());

        student.setAdmissionDate(request.getAdmissionDate());
    }

    public StudentResponse toResponse(Student student) {

        return StudentResponse.builder()

                .id(student.getId())

                .admissionNumber(student.getAdmissionNumber())

                .rollNumber(student.getRollNumber())

                .firstName(student.getFirstName())

                .lastName(student.getLastName())

                .fullName(buildFullName(student))

                .gender(student.getGender())

                .dateOfBirth(student.getDateOfBirth())

                .mobile(student.getMobile())

                .email(student.getEmail())

                .fatherName(student.getFatherName())

                .motherName(student.getMotherName())

                .guardianMobile(student.getGuardianMobile())

                .address(student.getAddress())

                .city(student.getCity())

                .state(student.getState())

                .pincode(student.getPincode())

                .photoPath(student.getPhotoPath())

                .admissionDate(student.getAdmissionDate())

                .status(student.getStatus())

                .active(student.getActive())

                .build();
    }

    public List<StudentResponse> toResponseList(
            List<Student> students) {

        return students
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private String buildFullName(Student student) {

        String firstName = student.getFirstName() == null
                ? ""
                : student.getFirstName().trim();

        String lastName = student.getLastName() == null
                ? ""
                : student.getLastName().trim();

        return (firstName + " " + lastName).trim();
    }

}