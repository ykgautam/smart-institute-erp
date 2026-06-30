package com.smartinstitute.erp.course.repository;

import com.smartinstitute.erp.course.entity.Course;
import com.smartinstitute.erp.institute.entity.Institute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CourseRepository extends
        JpaRepository<Course, Long>,
        JpaSpecificationExecutor<Course> {

    boolean existsByInstituteAndCourseCodeIgnoreCase(
            Institute institute,
            String courseCode
    );

    boolean existsByInstituteAndCourseNameIgnoreCase(
            Institute institute,
            String courseName
    );

    Optional<Course> findByIdAndActiveTrue(
            Long id
    );

    Optional<Course> findByIdAndInstituteAndActiveTrue(
            Long id,
            Institute institute
    );

}