package com.smartinstitute.erp.student.service.impl;

import com.smartinstitute.erp.common.exception.InvalidRequestException;
import com.smartinstitute.erp.common.exception.ResourceNotFoundException;
import com.smartinstitute.erp.common.pagination.PageResponse;
import com.smartinstitute.erp.common.pagination.PaginationRequest;
import com.smartinstitute.erp.common.pagination.PaginationUtils;
import com.smartinstitute.erp.common.service.BaseCrudService;
import com.smartinstitute.erp.common.specification.SpecificationBuilder;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.security.util.SecurityUtil;
import com.smartinstitute.erp.student.constants.StudentSortableFields;
import com.smartinstitute.erp.student.dto.CreateStudentRequest;
import com.smartinstitute.erp.student.dto.StudentResponse;
import com.smartinstitute.erp.student.dto.StudentStatusRequest;
import com.smartinstitute.erp.student.dto.UpdateStudentRequest;
import com.smartinstitute.erp.student.entity.Student;
import com.smartinstitute.erp.student.mapper.StudentMapper;
import com.smartinstitute.erp.student.repository.StudentRepository;
import com.smartinstitute.erp.student.service.StudentService;
import com.smartinstitute.erp.student.specification.StudentSpecification;
import com.smartinstitute.erp.student.validation.StudentValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class StudentServiceImpl extends BaseCrudService implements StudentService {

    private final StudentRepository studentRepository;

    private final StudentMapper studentMapper;

    private final StudentValidationService validationService;

    public StudentServiceImpl(SecurityUtil securityUtil, InstituteAccessValidator instituteAccessValidator, StudentRepository studentRepository, StudentMapper studentMapper, StudentValidationService validationService) {

        super(securityUtil, instituteAccessValidator);

        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
        this.validationService = validationService;
    }

    @Override
    public StudentResponse createStudent(CreateStudentRequest request) {

        Institute institute = getCurrentInstitute();

        validationService.validateForCreate(request, institute);

        Student student = studentMapper.toEntity(request, institute);

        student = studentRepository.save(student);

        log.info("Student created successfully. Admission No: {}", student.getAdmissionNumber());

        return studentMapper.toResponse(student);
    }

    @Override
    public StudentResponse updateStudent(Long id, UpdateStudentRequest request) {

        Student student = getStudent(id);

        validateOwnership(student.getInstitute().getId(), "Student");

        validationService.validateForUpdate(student, request, getCurrentInstitute());

        studentMapper.updateEntity(student, request);

        student = studentRepository.save(student);

        log.info("Student updated successfully. Id={}", student.getId());

        return studentMapper.toResponse(student);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long id) {

        Student student = getStudent(id);

        validateOwnership(student.getInstitute().getId(), "Student");

        return studentMapper.toResponse(student);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents1() {

        return studentMapper.toResponseList(studentRepository.findAllByInstituteAndActiveTrue(getCurrentInstitute()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> searchStudents(String keyword) {

        return studentMapper.toResponseList(studentRepository.searchByName(keyword, getCurrentInstitute()));
    }

    @Override
    public StudentResponse updateStatus(Long id, StudentStatusRequest request) {

        Student student = getStudent(id);

        validateOwnership(student.getInstitute().getId(), "Student");

        student.setStatus(request.getStatus());

        student = studentRepository.save(student);

        log.info("Student status updated. Id={}", student.getId());

        return studentMapper.toResponse(student);
    }

    @Override
    public void deleteStudent(Long id) {

        Student student = getStudent(id);

        validateOwnership(student.getInstitute().getId(), "Student");

        student.setActive(false);

        studentRepository.save(student);

        log.info("Student deleted. Id={}", student.getId());
    }

    private Student getStudent(Long id) {

        return studentRepository.findByIdAndActiveTrue(id).orElseThrow(() -> new ResourceNotFoundException("Student not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<StudentResponse> getStudents(PaginationRequest request) {

        Institute institute = getCurrentInstitute();

        if (!StudentSortableFields.ALLOWED_FIELDS.contains(request.getSortBy())) {

            throw new InvalidRequestException("Invalid sort field : " + request.getSortBy());
        }
        Pageable pageable = SpecificationBuilder.buildPageable(request);

        Specification<Student> specification = StudentSpecification.filterStudents(request.getKeyword(), institute);

        Page<Student> page = studentRepository.findAll(specification, pageable);

//        return PageResponse.<StudentResponse>builder().content(page.getContent().stream().map(studentMapper::toResponse).toList()).page(page.getNumber()).size(page.getSize()).totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).first(page.isFirst()).last(page.isLast()).numberOfElements(page.getNumberOfElements()).empty(page.isEmpty()).build();
        List<StudentResponse> responses =
                page.getContent()
                        .stream()
                        .map(studentMapper::toResponse)
                        .toList();

        return PaginationUtils.buildPageResponse(
                page,
                responses
        );
    }

}