package com.smartinstitute.erp.fee.service.impl;

import com.smartinstitute.erp.common.enums.fee.FeeStatus;
import com.smartinstitute.erp.common.exception.BadRequestException;
import com.smartinstitute.erp.common.exception.ResourceNotFoundException;
import com.smartinstitute.erp.common.service.BaseCrudService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.course.entity.Course;
import com.smartinstitute.erp.fee.dto.request.AssignStudentFeeRequest;
import com.smartinstitute.erp.fee.dto.response.StudentFeeResponse;
import com.smartinstitute.erp.fee.entity.FeeStructure;
import com.smartinstitute.erp.fee.entity.StudentFee;
import com.smartinstitute.erp.fee.mapper.StudentFeeMapper;
import com.smartinstitute.erp.fee.repository.FeeStructureRepository;
import com.smartinstitute.erp.fee.repository.StudentFeeRepository;
import com.smartinstitute.erp.fee.service.StudentFeeService;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.security.util.SecurityUtil;
import com.smartinstitute.erp.student.entity.Student;
import com.smartinstitute.erp.student.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class StudentFeeServiceImpl extends BaseCrudService
        implements StudentFeeService {

    private final StudentFeeRepository studentFeeRepository;
    private final FeeStructureRepository feeStructureRepository;
    private final StudentRepository studentRepository;
    private final StudentFeeMapper studentFeeMapper;

    public StudentFeeServiceImpl(
            SecurityUtil securityUtil,
            InstituteAccessValidator instituteAccessValidator,
            StudentFeeRepository studentFeeRepository,
            FeeStructureRepository feeStructureRepository,
            StudentRepository studentRepository,
            StudentFeeMapper studentFeeMapper) {

        super(securityUtil, instituteAccessValidator);

        this.studentFeeRepository = studentFeeRepository;
        this.feeStructureRepository = feeStructureRepository;
        this.studentRepository = studentRepository;
        this.studentFeeMapper = studentFeeMapper;
    }

    @Override
    public StudentFeeResponse assignFee(
            AssignStudentFeeRequest request) {

        Institute currentInstitute = getCurrentInstitute();

        Student student = studentRepository
                .findByIdAndInstituteAndActiveTrue(
                        request.getStudentId(),
                        currentInstitute
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found."
                        ));

        FeeStructure feeStructure = feeStructureRepository
                .findByIdAndInstituteAndActiveTrue(
                        request.getFeeStructureId(),
                        currentInstitute
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Fee Structure not found."
                        ));

        studentFeeRepository
                .findByStudentAndInstituteAndActiveTrue(
                        student,
                        currentInstitute
                )
                .ifPresent(fee -> {
                    throw new BadRequestException(
                            "Student fee already assigned."
                    );
                });

        /*
         * Student must belong to a batch
         */
        if (student.getBatch() == null) {

            throw new BadRequestException(
                    "Student must be assigned to a batch before fee assignment."
            );
        }

        Course studentCourse =
                student.getBatch().getCourse();

        if (studentCourse == null) {

            throw new BadRequestException(
                    "Student batch is not linked to any course."
            );
        }

        /*
         * Fee Structure must belong to
         * student's batch course.
         */
        if (!studentCourse.getId().equals(
                feeStructure.getCourse().getId())) {

            throw new BadRequestException(
                    "Fee Structure does not belong to student's batch course."
            );
        }

        BigDecimal discount = request.getDiscount() == null
                ? BigDecimal.ZERO
                : request.getDiscount();

        if (discount.compareTo(BigDecimal.ZERO) < 0) {

            throw new BadRequestException(
                    "Discount cannot be negative."
            );
        }

        if (discount.compareTo(
                feeStructure.getAmount()) > 0) {

            throw new BadRequestException(
                    "Discount cannot be greater than total fee."
            );
        }

        BigDecimal finalFee =
                feeStructure.getAmount()
                        .subtract(discount);

//        StudentFee studentFee = studentFeeMapper.toEntity(request);

        StudentFee studentFee = new StudentFee();

        studentFee.setStudent(student);

        studentFee.setFeeStructure(feeStructure);

        studentFee.setInstitute(currentInstitute);

        studentFee.setTotalFee(
                feeStructure.getAmount()
        );

        studentFee.setDiscount(discount);

        studentFee.setFinalFee(finalFee);

        studentFee.setPaidAmount(
                BigDecimal.ZERO
        );

        studentFee.setPendingAmount(finalFee);

        studentFee.setStatus(
                FeeStatus.PENDING
        );

        studentFee.setActive(true);

        studentFee = studentFeeRepository.save(
                studentFee
        );

        return studentFeeMapper.toResponse(
                studentFee
        );
    }

    @Override
    public StudentFeeResponse getStudentFee(
            Long studentId) {

        Institute currentInstitute = getCurrentInstitute();

        Student student = studentRepository
                .findByIdAndInstituteAndActiveTrue(
                        studentId,
                        currentInstitute
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found."
                        ));
        StudentFee studentFee = studentFeeRepository
                .findByStudentAndInstituteAndActiveTrue(
                        student,
                        currentInstitute
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student fee not found."
                        ));

        return studentFeeMapper.toResponse(
                studentFee
        );
    }

    @Override
    public List<StudentFeeResponse> getAllStudentFees() {

        Institute currentInstitute = getCurrentInstitute();

        List<StudentFee> studentFees =
                studentFeeRepository
                        .findByInstituteAndActiveTrue(
                                currentInstitute
                        );

        return studentFees
                .stream()
                .map(studentFeeMapper::toResponse)
                .toList();
    }

}