package com.smartinstitute.erp.student.specification;

import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.student.entity.Student;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class StudentSpecification {

    private StudentSpecification() {
    }

    public static Specification<Student> filterStudents(
            String keyword,
            Institute institute) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            /*
             * Only active students
             */
            predicates.add(
                    cb.isTrue(root.get("active"))
            );

            /*
             * Logged-in institute only
             */
            predicates.add(
                    cb.equal(
                            root.get("institute"),
                            institute
                    )
            );

            /*
             * Global Search
             */
            if (keyword != null && !keyword.isBlank()) {

                String pattern = "%" + keyword.trim().toLowerCase() + "%";

                Predicate firstName =
                        cb.like(
                                cb.lower(root.get("firstName")),
                                pattern
                        );

                Predicate lastName =
                        cb.like(
                                cb.lower(root.get("lastName")),
                                pattern
                        );

                Predicate admissionNumber =
                        cb.like(
                                cb.lower(root.get("admissionNumber")),
                                pattern
                        );

                Predicate rollNumber =
                        cb.like(
                                cb.lower(root.get("rollNumber")),
                                pattern
                        );

                Predicate mobile =
                        cb.like(
                                cb.lower(root.get("mobile")),
                                pattern
                        );

                Predicate email =
                        cb.like(
                                cb.lower(root.get("email")),
                                pattern
                        );

                Predicate city =
                        cb.like(
                                cb.lower(root.get("city")),
                                pattern
                        );

                predicates.add(
                        cb.or(
                                firstName,
                                lastName,
                                admissionNumber,
                                rollNumber,
                                mobile,
                                email,
                                city
                        )
                );
            }

            return cb.and(
                    predicates.toArray(new Predicate[0])
            );

        };

    }

}