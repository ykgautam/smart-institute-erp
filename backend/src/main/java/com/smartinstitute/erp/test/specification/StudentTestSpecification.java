package com.smartinstitute.erp.test.specification;

import com.smartinstitute.erp.common.enums.test.StudentTestStatus;
import com.smartinstitute.erp.student.entity.Student;
import com.smartinstitute.erp.test.entity.StudentTest;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
//import java.util.function.Predicate;

public class StudentTestSpecification {

    public static Specification<StudentTest> filter(

            Student student,

            StudentTestStatus status,

            LocalDate fromDate,

            LocalDate toDate,
            String search,

            String sort,

            String direction) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(
                    cb.equal(root.get("student"), student)
            );

            if (status != null) {

                predicates.add(
                        cb.equal(root.get("status"), status)
                );
            }

            if (fromDate != null) {

                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("submittedAt"),
                                fromDate.atStartOfDay()
                        )
                );
            }

            if (toDate != null) {

                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("submittedAt"),
                                toDate.atTime(23,59,59)
                        )
                );
            }

            if (search != null && !search.isBlank()) {

                predicates.add(

                        cb.like(

                                cb.lower(
                                        root.get("test")
                                                .get("title")
                                ),

                                "%" + search.toLowerCase() + "%"
                        )
                );
            }

//            query.orderBy(
//                    cb.desc(root.get("submittedAt"))
//            );
            Path<?> sortField;

            switch (sort) {

                case "percentage":
                    sortField = root.get("percentage");
                    break;

                case "obtainedMarks":
                    sortField = root.get("obtainedMarks");
                    break;

                case "startedAt":
                    sortField = root.get("startedAt");
                    break;

                default:
                    sortField = root.get("submittedAt");
            }

            if ("asc".equalsIgnoreCase(direction)) {

                query.orderBy(
                        cb.asc(sortField)
                );

            } else {

                query.orderBy(
                        cb.desc(sortField)
                );
            }

            return cb.and(
                    predicates.toArray(new Predicate[0])
            );
        };
    }

}