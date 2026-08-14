package com.smartinstitute.erp.attendance.repository;

import com.smartinstitute.erp.attendance.entity.Attendance;
import com.smartinstitute.erp.batch.entity.Batch;
import com.smartinstitute.erp.common.enums.attendance.AttendanceStatus;
import com.smartinstitute.erp.dashboard.admin.projection.AttendanceTrendProjection;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

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

    long countByStudentAndStatus(Student student, AttendanceStatus status);

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

//    long countByInstituteAndAttendanceDate(
//            Institute institute,
//            LocalDate attendanceDate
//    );
//
//    long countByInstituteAndAttendanceDateAndStatus(
//            Institute institute,
//            LocalDate attendanceDate,
//            AttendanceStatus status
//    );

    long countByBatch_InstituteAndAttendanceDate(
            Institute institute,
            LocalDate attendanceDate
    );

    long countByBatch_InstituteAndAttendanceDateAndStatus(
            Institute institute,
            LocalDate attendanceDate,
            AttendanceStatus status
    );

    @Query(value = """
        SELECT
            EXTRACT(YEAR FROM a.attendance_date)::INTEGER AS year,
            EXTRACT(MONTH FROM a.attendance_date)::INTEGER AS month,

            ROUND(
                (
                    SUM(
                        CASE
                            WHEN a.status = 'PRESENT' THEN 1
                            ELSE 0
                        END
                    ) * 100.0
                ) / NULLIF(COUNT(a.id), 0),
                2
            ) AS attendancePercentage

        FROM attendance a

        INNER JOIN batches b
                ON a.batch_id = b.id

        WHERE b.institute_id = :#{#institute.id}

        GROUP BY
            EXTRACT(YEAR FROM a.attendance_date),
            EXTRACT(MONTH FROM a.attendance_date)

        ORDER BY
            year,
            month
        """,
            nativeQuery = true)
    List<AttendanceTrendProjection> getAttendanceTrend(
            Institute institute
    );

}