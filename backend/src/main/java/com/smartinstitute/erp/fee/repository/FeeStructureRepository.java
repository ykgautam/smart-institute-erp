package com.smartinstitute.erp.fee.repository;

import com.smartinstitute.erp.course.entity.Course;
import com.smartinstitute.erp.fee.entity.FeeStructure;
import com.smartinstitute.erp.institute.entity.Institute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeeStructureRepository
        extends JpaRepository<FeeStructure, Long> {

    Optional<FeeStructure> findByIdAndInstituteAndActiveTrue(
            Long id,
            Institute institute
    );

    Optional<FeeStructure> findByCourseAndInstituteAndActiveTrue(
            Course course,
            Institute institute
    );

    boolean existsByCourseAndInstitute(
            Course course,
            Institute institute
    );

    List<FeeStructure> findByInstituteAndActiveTrue(
            Institute institute
    );

    boolean existsByCourseAndInstituteAndActiveTrue(Course course, Institute institute);
}