package com.smartinstitute.erp.course.specification;

import com.smartinstitute.erp.course.entity.Course;
import com.smartinstitute.erp.institute.entity.Institute;
import org.springframework.data.jpa.domain.Specification;

public final class CourseSpecification {

    private CourseSpecification() {
    }

    public static Specification<Course> search(
            Institute institute,
            String keyword) {

        return (root, query, cb) -> {

            Specification<Course> specification =
                    Specification.where(hasInstitute(institute))
                            .and(isActive());

            if (keyword != null && !keyword.isBlank()) {
                specification = specification.and(
                        containsKeyword(keyword)
                );
            }

            return specification.toPredicate(
                    root,
                    query,
                    cb
            );
        };
    }

    public static Specification<Course> hasInstitute(
            Institute institute) {

        return (root, query, cb) ->
                cb.equal(
                        root.get("institute"),
                        institute
                );
    }

    public static Specification<Course> isActive() {

        return (root, query, cb) ->
                cb.isTrue(root.get("active"));
    }

    public static Specification<Course> containsKeyword(
            String keyword) {

        return (root, query, cb) -> {

            String search =
                    "%" + keyword.toLowerCase().trim() + "%";

            return cb.or(

                    cb.like(
                            cb.lower(root.get("courseCode")),
                            search
                    ),

                    cb.like(
                            cb.lower(root.get("courseName")),
                            search
                    ),

                    cb.like(
                            cb.lower(root.get("description")),
                            search
                    )

            );
        };
    }

}