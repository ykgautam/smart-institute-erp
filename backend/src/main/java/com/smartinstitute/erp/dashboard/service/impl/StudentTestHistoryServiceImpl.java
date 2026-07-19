package com.smartinstitute.erp.dashboard.service.impl;

import com.smartinstitute.erp.common.enums.test.StudentTestStatus;
import com.smartinstitute.erp.common.service.StudentBaseService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.dashboard.dto.response.StudentTestHistoryResponse;
import com.smartinstitute.erp.dashboard.service.StudentTestHistoryService;
import com.smartinstitute.erp.security.util.SecurityUtil;
import com.smartinstitute.erp.student.entity.Student;
import com.smartinstitute.erp.student.repository.StudentRepository;
import com.smartinstitute.erp.test.entity.StudentTest;
import com.smartinstitute.erp.test.repository.StudentTestRepository;
import com.smartinstitute.erp.test.specification.StudentTestSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
public class StudentTestHistoryServiceImpl
        extends StudentBaseService
        implements StudentTestHistoryService {

    private final StudentTestRepository studentTestRepository;

    public StudentTestHistoryServiceImpl(
            SecurityUtil securityUtil,
            InstituteAccessValidator instituteAccessValidator,
            StudentRepository studentRepository,
            StudentTestRepository studentTestRepository) {

        super(
                securityUtil,
                instituteAccessValidator,
                studentRepository
        );

        this.studentTestRepository = studentTestRepository;
    }

    @Override
    public Page<StudentTestHistoryResponse> getHistory(

            int page,

            int size,

            StudentTestStatus status,

            LocalDate fromDate,

            LocalDate toDate,
            String search,

            String sort,

            String direction) {

        Student student = getCurrentStudent();

        Pageable pageable =
                PageRequest.of(page, size);

        Specification<StudentTest> specification =
                StudentTestSpecification.filter(

                        student,

                        status,

                        fromDate,

                        toDate,

                        search,

                        sort,

                        direction
                );

        return studentTestRepository
                .findAll(specification, pageable)
                .map(this::toResponse);
    }

    @Override
    public Page<StudentTest> findByStudentOrderBySubmittedAtDesc(Student student, Pageable pageable) {
        return null;
    }

    private StudentTestHistoryResponse toResponse(
            StudentTest studentTest) {

        return StudentTestHistoryResponse.builder()

                .studentTestId(studentTest.getId())

                .testId(studentTest.getTest().getId())

                .testTitle(studentTest.getTest().getTitle())

                .percentage(studentTest.getPercentage())

                .obtainedMarks(studentTest.getObtainedMarks())

                .totalMarks(studentTest.getTotalMarks())

                .passed(studentTest.getPassed())

                .status(studentTest.getStatus())

                .startedAt(studentTest.getStartedAt())

                .submittedAt(studentTest.getSubmittedAt())

                .build();
    }
}