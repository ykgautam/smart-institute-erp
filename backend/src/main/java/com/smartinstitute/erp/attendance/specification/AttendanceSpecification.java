package com.smartinstitute.erp.attendance.specification;

import com.smartinstitute.erp.attendance.entity.Attendance;
import com.smartinstitute.erp.batch.entity.Batch;
import com.smartinstitute.erp.student.entity.Student;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class AttendanceSpecification {

    private AttendanceSpecification() {
    }

    public static Specification<Attendance> hasStudent(Student student) {

        return (root, query, cb) ->
                student == null
                        ? null
                        : cb.equal(root.get("student"), student);
    }

    public static Specification<Attendance> hasBatch(Batch batch) {

        return (root, query, cb) ->
                batch == null
                        ? null
                        : cb.equal(root.get("batch"), batch);
    }

    public static Specification<Attendance> attendanceDate(LocalDate date) {

        return (root, query, cb) ->
                date == null
                        ? null
                        : cb.equal(root.get("attendanceDate"), date);
    }

    public static Specification<Attendance> attendanceBetween(
            LocalDate startDate,
            LocalDate endDate) {

        return (root, query, cb) -> {

            if (startDate == null || endDate == null) {
                return null;
            }

            return cb.between(
                    root.get("attendanceDate"),
                    startDate,
                    endDate
            );
        };
    }

}