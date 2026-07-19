package com.smartinstitute.erp.test.repository;

import com.smartinstitute.erp.common.enums.test.TestStatus;
import com.smartinstitute.erp.course.entity.Course;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.test.entity.Test;
import com.smartinstitute.erp.test.entity.Topic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TestRepository extends JpaRepository<Test, Long> {

    Optional<Test> findByIdAndInstituteAndActiveTrue(
            Long id,
            Institute institute
    );

    List<Test> findByInstituteAndActiveTrue(
            Institute institute
    );

    List<Test> findByCourseAndInstituteAndActiveTrue(
            Course course,
            Institute institute
    );

    List<Test> findByTopicAndInstituteAndActiveTrue(
            Topic topic,
            Institute institute
    );

    List<Test> findByCourseAndTopicAndInstituteAndActiveTrue(
            Course course,
            Topic topic,
            Institute institute
    );

    boolean existsByInstituteAndCourseAndTopicAndTitleAndActiveTrue(
            Institute institute,
            Course course,
            Topic topic,
            String title
    );

    boolean existsByInstituteAndCourseAndTopicAndTitleAndIdNotAndActiveTrue(
            Institute institute,
            Course course,
            Topic topic,
            String title,
            Long id
    );

    Page<Test> findByInstituteAndActiveTrue(
            Institute institute,
            Pageable pageable
    );

    long countByStatus(TestStatus status);

    //    This fetches the next 5 published tests.
    List<Test> findTop5ByStatusAndStartTimeAfterOrderByStartTimeAsc(
            TestStatus status,
            LocalDateTime currentTime
    );
}