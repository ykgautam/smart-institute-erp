package com.smartinstitute.erp.test.repository;

import com.smartinstitute.erp.course.entity.Course;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.test.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    Optional<Topic> findByIdAndInstituteAndActiveTrue(
            Long id,
            Institute institute
    );

    List<Topic> findByInstituteAndActiveTrue(
            Institute institute
    );

    List<Topic> findByCourseAndInstituteAndActiveTrue(
            Course course,
            Institute institute
    );

    boolean existsByCourseAndInstituteAndNameIgnoreCaseAndActiveTrue(
            Course course,
            Institute institute,
            String name
    );

    boolean existsByInstituteAndCourseAndNameAndActiveTrue(
            Institute institute,
            Course course,
            String name
    );

    boolean existsByInstituteAndCourseAndNameAndIdNotAndActiveTrue(
            Institute institute,
            Course course,
            String name,
            Long id
    );

    Optional<Topic> findByIdAndActiveTrue(
            Long id
    );

    List<Topic> findByInstituteAndActiveTrueOrderByDisplayOrderAscNameAsc(
            Institute institute
    );

    List<Topic> findByCourseAndActiveTrueOrderByDisplayOrderAscNameAsc(
            Course course
    );
}