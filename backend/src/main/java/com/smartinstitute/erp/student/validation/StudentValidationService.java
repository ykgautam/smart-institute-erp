package com.smartinstitute.erp.student.validation;

import com.smartinstitute.erp.common.exception.DuplicateResourceException;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.student.dto.CreateStudentRequest;
import com.smartinstitute.erp.student.dto.UpdateStudentRequest;
import com.smartinstitute.erp.student.entity.Student;
import com.smartinstitute.erp.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentValidationService {

    private final StudentRepository studentRepository;

    public void validateForCreate(
            CreateStudentRequest request,
            Institute institute) {

        validateAdmissionNumber(
                request.getAdmissionNumber(),
                institute);

        validateMobile(
                request.getMobile(),
                institute);

        validateEmail(
                request.getEmail(),
                institute);
    }

    public void validateForUpdate(
            Student student,
            UpdateStudentRequest request,
            Institute institute) {

        if (request.getMobile() != null
                && !request.getMobile().equals(student.getMobile())) {

            validateMobile(
                    request.getMobile(),
                    institute);
        }

        if (request.getEmail() != null
                && !request.getEmail().equals(student.getEmail())) {

            validateEmail(
                    request.getEmail(),
                    institute);
        }
    }

    public void validateAdmissionNumber(
            String admissionNumber,
            Institute institute) {

        if (studentRepository.existsByAdmissionNumberAndInstitute(
                admissionNumber,
                institute)) {

            throw new DuplicateResourceException(
                    "Admission number already exists.");
        }
    }

    public void validateMobile(
            String mobile,
            Institute institute) {

        if (mobile == null || mobile.isBlank()) {
            return;
        }

        if (studentRepository.existsByMobileAndInstitute(
                mobile,
                institute)) {

            throw new DuplicateResourceException(
                    "Mobile already exists.");
        }
    }

    public void validateEmail(
            String email,
            Institute institute) {

        if (email == null || email.isBlank()) {
            return;
        }

        if (studentRepository.existsByEmailAndInstitute(
                email,
                institute)) {

            throw new DuplicateResourceException(
                    "Email already exists.");
        }
    }

}