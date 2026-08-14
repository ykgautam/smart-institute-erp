package com.smartinstitute.erp.fee.repository;

import com.smartinstitute.erp.common.enums.fee.FeeStatus;
import com.smartinstitute.erp.fee.entity.StudentFee;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentFeeRepository
        extends JpaRepository<StudentFee, Long> {

    Optional<StudentFee> findByIdAndInstituteAndActiveTrue(
            Long id,
            Institute institute
    );

    Optional<StudentFee> findByStudentAndInstituteAndActiveTrue(
            Student student,
            Institute institute
    );

    List<StudentFee> findByInstituteAndActiveTrue(
            Institute institute
    );

    Optional<StudentFee> findByStudentIdAndInstituteAndActiveTrue(
            Long studentId,
            Institute institute
    );

    long countByInstituteAndStatus(
            Institute institute,
            FeeStatus feeStatus
    );
}