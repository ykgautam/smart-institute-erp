package com.smartinstitute.erp.attendance.repository;

import com.smartinstitute.erp.attendance.entity.Attendance;
import com.smartinstitute.erp.batch.entity.Batch;
import com.smartinstitute.erp.common.enums.attendance.AttendanceStatus;
import com.smartinstitute.erp.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository
        extends JpaRepository<Attendance, Long>,
        JpaSpecificationExecutor<Attendance> {

    boolean existsByStudentAndAttendanceDate(
            Student student,
            LocalDate attendanceDate
    );

    Optional<Attendance> findByStudentAndAttendanceDate(
            Student student,
            LocalDate attendanceDate
    );

    List<Attendance> findByBatchAndAttendanceDateOrderByStudentFirstNameAsc(
            Batch batch,
            LocalDate attendanceDate
    );

    List<Attendance> findByStudentOrderByAttendanceDateDesc(
            Student student
    );

    List<Attendance> findByBatchOrderByAttendanceDateDesc(
            Batch batch
    );

    long countByStudent(Student student);

    long countByStudentAndStatus(
            Student student,
            AttendanceStatus status
    );

    List<Attendance> findByStudent(Student student);

    List<Attendance> findByBatch(Batch batch);

    List<Attendance> findByStudentAndAttendanceDateBetween(
            Student student,
            LocalDate from,
            LocalDate to
    );

    List<Attendance>
    findByBatchAndAttendanceDateBetween(
            Batch batch,
            LocalDate from,
            LocalDate to
    );
}