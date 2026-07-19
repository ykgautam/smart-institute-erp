package com.smartinstitute.erp.common.service;

import com.smartinstitute.erp.common.exception.ResourceNotFoundException;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.security.util.SecurityUtil;
import com.smartinstitute.erp.student.entity.Student;
import com.smartinstitute.erp.student.repository.StudentRepository;

public abstract class StudentBaseService extends BaseCrudService {

    protected final StudentRepository studentRepository;

    protected StudentBaseService(
            SecurityUtil securityUtil,
            InstituteAccessValidator instituteAccessValidator,
            StudentRepository studentRepository) {

        super(securityUtil, instituteAccessValidator);

        this.studentRepository = studentRepository;
    }

    protected Student getCurrentStudent() {

        Long userId = securityUtil.getCurrentUserId();

        return studentRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found."));
    }
}