package com.smartinstitute.erp.student.repository;

import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepository
        extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {

    Optional<Student> findByIdAndActiveTrue(Long id);

    List<Student> findAllByActiveTrue();

    List<Student> findAllByInstituteAndActiveTrue(
            Institute institute
    );

    Optional<Student> findByAdmissionNumberAndInstituteAndActiveTrue(
            String admissionNumber,
            Institute institute
    );

    Optional<Student> findByMobileAndInstituteAndActiveTrue(
            String mobile,
            Institute institute
    );

    Optional<Student> findByEmailAndInstituteAndActiveTrue(
            String email,
            Institute institute
    );

    boolean existsByAdmissionNumberAndInstitute(
            String admissionNumber,
            Institute institute
    );

    boolean existsByMobileAndInstitute(
            String mobile,
            Institute institute
    );

    boolean existsByEmailAndInstitute(
            String email,
            Institute institute
    );

    List<Student> findByFirstNameContainingIgnoreCaseAndInstituteAndActiveTrue(
            String firstName,
            Institute institute
    );

    List<Student> findByLastNameContainingIgnoreCaseAndInstituteAndActiveTrue(
            String lastName,
            Institute institute
    );

    @Query("""
            SELECT s
            FROM Student s
            WHERE s.institute = :institute
            AND s.active = true
            AND (
                 LOWER(s.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(CONCAT(s.firstName,' ',s.lastName))
                    LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            """)
    List<Student> searchByName(
            @Param("keyword") String keyword,
            @Param("institute") Institute institute
    );

}