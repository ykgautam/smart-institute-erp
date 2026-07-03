package com.smartinstitute.erp.attendance.service.impl;

import com.smartinstitute.erp.attendance.dto.request.AttendanceEntryRequest;
import com.smartinstitute.erp.attendance.dto.request.MarkAttendanceRequest;
import com.smartinstitute.erp.attendance.dto.request.UpdateAttendanceRequest;
import com.smartinstitute.erp.attendance.dto.response.AttendanceMonthlyReportResponse;
import com.smartinstitute.erp.attendance.dto.response.AttendanceResponse;
import com.smartinstitute.erp.attendance.dto.response.AttendanceSummaryResponse;
import com.smartinstitute.erp.attendance.dto.response.BatchAttendanceReportResponse;
import com.smartinstitute.erp.attendance.entity.Attendance;
import com.smartinstitute.erp.attendance.mapper.AttendanceMapper;
import com.smartinstitute.erp.attendance.repository.AttendanceRepository;
import com.smartinstitute.erp.attendance.service.AttendanceService;
import com.smartinstitute.erp.attendance.util.AttendanceStatistics;
import com.smartinstitute.erp.batch.entity.Batch;
import com.smartinstitute.erp.batch.repository.BatchRepository;
import com.smartinstitute.erp.common.exception.BadRequestException;
import com.smartinstitute.erp.common.exception.ResourceNotFoundException;
import com.smartinstitute.erp.common.service.BaseCrudService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.security.util.SecurityUtil;
import com.smartinstitute.erp.student.entity.Student;
import com.smartinstitute.erp.student.repository.StudentRepository;
import com.smartinstitute.erp.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class AttendanceServiceImpl extends BaseCrudService implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceMapper attendanceMapper;
    private final BatchRepository batchRepository;
    private final StudentRepository studentRepository;

    public AttendanceServiceImpl(SecurityUtil securityUtil, InstituteAccessValidator instituteAccessValidator,
                                 AttendanceRepository attendanceRepository, AttendanceMapper attendanceMapper,
                                 BatchRepository batchRepository, StudentRepository studentRepository) {
        super(securityUtil, instituteAccessValidator);
        this.attendanceRepository = attendanceRepository;
        this.attendanceMapper = attendanceMapper;
        this.batchRepository = batchRepository;
        this.studentRepository = studentRepository;

    }

    @Override
    public List<AttendanceResponse> markAttendance(
            MarkAttendanceRequest request) {

        Institute currentInstitute = getCurrentInstitute();

        User currentUser =
                securityUtil.getCurrentUser();

        Batch batch = batchRepository
                .findByIdAndInstituteAndActiveTrue(
                        request.getBatchId(),
                        currentInstitute
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Batch not found."
                        ));

        if (request.getAttendanceDate().isAfter(LocalDate.now())) {

            throw new BadRequestException(
                    "Attendance cannot be marked for a future date."
            );
        }

        List<AttendanceResponse> responses = new ArrayList<>();

        for (AttendanceEntryRequest entry : request.getAttendanceList()) {

            Student student = studentRepository
                    .findByIdAndInstituteAndActiveTrue(
                            entry.getStudentId(),
                            currentInstitute
                    )
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Student not found."
                            ));

            if (student.getBatch() == null
                    || !student.getBatch().getId().equals(batch.getId())) {

                throw new BadRequestException(
                        "Student does not belong to the selected batch."
                );
            }

            if (attendanceRepository.existsByStudentAndAttendanceDate(
                    student,
                    request.getAttendanceDate())) {

                throw new BadRequestException(
                        "Attendance already marked for student: "
                                + student.getFirstName()
                                + " "
                                + (student.getLastName() == null
                                ? ""
                                : student.getLastName())
                );
            }

            Attendance attendance =
                    attendanceMapper.toEntity(entry);

            attendance.setStudent(student);
            attendance.setBatch(batch);
            attendance.setAttendanceDate(
                    request.getAttendanceDate()
            );
            attendance.setMarkedBy(currentUser);

            Attendance savedAttendance =
                    attendanceRepository.save(attendance);

            responses.add(
                    attendanceMapper.toResponse(savedAttendance)
            );
        }

        return responses;
    }

    @Override
    public AttendanceResponse updateAttendance(
            Long attendanceId,
            UpdateAttendanceRequest request) {

        Institute currentInstitute = getCurrentInstitute();

        Attendance attendance =
                attendanceRepository
                        .findById(attendanceId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Attendance not found."
                                ));

        if (!attendance.getBatch()
                .getInstitute()
                .getId()
                .equals(currentInstitute.getId())) {

            throw new ResourceNotFoundException(
                    "Attendance not found."
            );
        }

        attendanceMapper.updateEntity(
                attendance,
                request
        );

        Attendance updatedAttendance =
                attendanceRepository.save(attendance);

        return attendanceMapper.toResponse(
                updatedAttendance
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getBatchAttendance(
            Long batchId,
            LocalDate attendanceDate) {

        Institute currentInstitute = getCurrentInstitute();

        Batch batch = batchRepository
                .findByIdAndInstituteAndActiveTrue(
                        batchId,
                        currentInstitute
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Batch not found."
                        ));

        List<Attendance> attendances =
                attendanceRepository
                        .findByBatchAndAttendanceDateOrderByStudentFirstNameAsc(
                                batch,
                                attendanceDate
                        );

        return attendances.stream()
                .map(attendanceMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getStudentAttendanceHistory(
            Long studentId) {

        Institute currentInstitute = getCurrentInstitute();

        Student student = studentRepository
                .findByIdAndInstituteAndActiveTrue(
                        studentId,
                        currentInstitute
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found."
                        ));

        List<Attendance> attendances =
                attendanceRepository
                        .findByStudentOrderByAttendanceDateDesc(
                                student
                        );

        return attendances.stream()
                .map(attendanceMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceSummaryResponse getAttendanceSummary(
            Long studentId) {

        Institute currentInstitute = getCurrentInstitute();

        Student student =
                studentRepository
                        .findByIdAndInstituteAndActiveTrue(
                                studentId,
                                currentInstitute
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Student not found."
                                ));

        List<Attendance> attendances =
                attendanceRepository.findByStudent(student);

        int totalClasses = attendances.size();

        int presentCount = 0;
        int absentCount = 0;
        int leaveCount = 0;
        int lateCount = 0;

        for (Attendance attendance : attendances) {

            switch (attendance.getStatus()) {

                case PRESENT -> presentCount++;

                case ABSENT -> absentCount++;

                case LEAVE -> leaveCount++;

                case LATE -> lateCount++;
            }
        }

        double percentage = totalClasses == 0
                ? 0.0
                : (presentCount + lateCount) * 100.0 / totalClasses;

        return AttendanceSummaryResponse.builder()
                .studentId(student.getId())
                .studentName(
                        student.getFirstName() + " " +
                                (student.getLastName() == null
                                        ? ""
                                        : student.getLastName())
                )
                .totalClasses(totalClasses)
                .totalPresent(presentCount)
                .totalAbsent(absentCount)
                .totalLate(lateCount)
                .totalLeave(leaveCount)
                .attendancePercentage(
                        Math.round(percentage * 100.0) / 100.0
                )
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BatchAttendanceReportResponse> getBatchAttendanceReport(
            Long batchId) {

        Institute currentInstitute = getCurrentInstitute();

        Batch batch = batchRepository
                .findByIdAndInstituteAndActiveTrue(
                        batchId,
                        currentInstitute
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Batch not found."
                        ));

        List<Attendance> attendances =
                attendanceRepository.findByBatch(batch);

        Map<Student, List<Attendance>> attendanceMap =
                attendances.stream()
                        .collect(Collectors.groupingBy(
                                Attendance::getStudent
                        ));

        List<BatchAttendanceReportResponse> response =
                new ArrayList<>();

        for (Map.Entry<Student, List<Attendance>> entry
                : attendanceMap.entrySet()) {

            Student student = entry.getKey();

            List<Attendance> studentAttendance =
                    entry.getValue();

            int totalClasses = studentAttendance.size();

            int totalPresent = 0;
            int totalAbsent = 0;
            int totalLate = 0;
            int totalLeave = 0;

            for (Attendance attendance : studentAttendance) {

                switch (attendance.getStatus()) {

                    case PRESENT -> totalPresent++;

                    case ABSENT -> totalAbsent++;

                    case LATE -> totalLate++;

                    case LEAVE -> totalLeave++;
                }
            }

            double attendancePercentage =
                    totalClasses == 0
                            ? 0.0
                            : ((double) (totalPresent + totalLate)
                            / totalClasses) * 100;

            response.add(
                    BatchAttendanceReportResponse.builder()
                            .studentId(student.getId())
                            .studentName(
                                    student.getFirstName()
                                            + " "
                                            + (student.getLastName() == null
                                            ? ""
                                            : student.getLastName())
                            )
                            .totalClasses(totalClasses)
                            .totalPresent(totalPresent)
                            .totalAbsent(totalAbsent)
                            .totalLate(totalLate)
                            .totalLeave(totalLeave)
                            .attendancePercentage(
                                    Math.round(attendancePercentage * 100.0)
                                            / 100.0
                            )
                            .build()
            );
        }

        response.sort(
                Comparator.comparing(
                        BatchAttendanceReportResponse::getStudentName
                )
        );

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceMonthlyReportResponse getMonthlyAttendanceReport(
            Long studentId,
            Integer year,
            Integer month) {

        Institute currentInstitute = getCurrentInstitute();

        Student student = studentRepository
                .findByIdAndInstituteAndActiveTrue(
                        studentId,
                        currentInstitute
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found."
                        ));

        LocalDate from =
                LocalDate.of(year, month, 1);

        LocalDate to =
                from.withDayOfMonth(from.lengthOfMonth());

        List<Attendance> attendances =
                attendanceRepository.findByStudentAndAttendanceDateBetween(
                        student,
                        from,
                        to
                );

        int totalClasses = attendances.size();

        int totalPresent = 0;
        int totalAbsent = 0;
        int totalLate = 0;
        int totalLeave = 0;

        for (Attendance attendance : attendances) {

            switch (attendance.getStatus()) {

                case PRESENT -> totalPresent++;

                case ABSENT -> totalAbsent++;

                case LATE -> totalLate++;

                case LEAVE -> totalLeave++;
            }
        }

        double attendancePercentage =
                totalClasses == 0
                        ? 0.0
                        : ((double) (totalPresent + totalLate)
                        / totalClasses) * 100;

        return AttendanceMonthlyReportResponse.builder()
                .studentId(student.getId())
                .studentName(
                        student.getFirstName() + " "
                                + (student.getLastName() == null
                                ? ""
                                : student.getLastName())
                )
                .year(year)
                .month(month)
                .totalClasses(totalClasses)
                .totalPresent(totalPresent)
                .totalAbsent(totalAbsent)
                .totalLate(totalLate)
                .totalLeave(totalLeave)
                .attendancePercentage(
                        Math.round(attendancePercentage * 100.0) / 100.0
                )
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BatchAttendanceReportResponse> getBatchAttendanceReport(
            Long batchId,
            LocalDate from,
            LocalDate to) {

        Institute currentInstitute = getCurrentInstitute();

        Batch batch = batchRepository
                .findByIdAndInstituteAndActiveTrue(
                        batchId,
                        currentInstitute
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Batch not found."
                        ));

        List<Attendance> attendances =
                attendanceRepository
                        .findByBatchAndAttendanceDateBetween(
                                batch,
                                from,
                                to
                        );

        Map<Student, List<Attendance>> attendanceMap =
                attendances.stream()
                        .collect(Collectors.groupingBy(
                                Attendance::getStudent
                        ));

        List<BatchAttendanceReportResponse> response =
                new ArrayList<>();

        for (Map.Entry<Student, List<Attendance>> entry
                : attendanceMap.entrySet()) {

            Student student = entry.getKey();

            List<Attendance> studentAttendances =
                    entry.getValue();

            AttendanceStatistics statistics = calculateStatistics(studentAttendances);
//or
//            int totalClasses = studentAttendances.size();
//            int totalPresent = 0;
//            int totalAbsent = 0;
//            int totalLate = 0;
//            int totalLeave = 0;
//            for (Attendance attendance : studentAttendances) {
//
//                switch (attendance.getStatus()) {
//
//                    case PRESENT -> totalPresent++;
//
//                    case ABSENT -> totalAbsent++;
//
//                    case LATE -> totalLate++;
//
//                    case LEAVE -> totalLeave++;
//                }
//            }
//
//            double attendancePercentage =
//                    totalClasses == 0
//                            ? 0.0
//                            : ((double) (totalPresent + totalLate)
//                            / totalClasses) * 100;

            response.add(
                    BatchAttendanceReportResponse.builder()
                            .studentId(student.getId())
                            .studentName(
                                    student.getFirstName() + " "
                                            + (student.getLastName() == null
                                            ? ""
                                            : student.getLastName())
                            )
                            .totalClasses(statistics.getTotalClasses())
                            .totalPresent(statistics.getTotalPresent())
                            .totalAbsent(statistics.getTotalAbsent())
                            .totalLate(statistics.getTotalLate())
                            .totalLeave(statistics.getTotalLeave())
                            .attendancePercentage(statistics.getAttendancePercentage())
                            .build()
            );
        }

        response.sort(
                Comparator.comparing(
                        BatchAttendanceReportResponse::getStudentName
                )
        );

        return response;
    }

    private AttendanceStatistics calculateStatistics(List<Attendance> attendances) {

        int totalClasses = attendances.size();

        int totalPresent = 0;
        int totalAbsent = 0;
        int totalLate = 0;
        int totalLeave = 0;

        for (Attendance attendance : attendances) {

            switch (attendance.getStatus()) {

                case PRESENT -> totalPresent++;

                case ABSENT -> totalAbsent++;

                case LATE -> totalLate++;

                case LEAVE -> totalLeave++;
            }
        }

        double attendancePercentage =
                totalClasses == 0
                        ? 0.0
                        : ((double) (totalPresent + totalLate)
                        / totalClasses) * 100;

        attendancePercentage =
                Math.round(attendancePercentage * 100.0) / 100.0;

        return new AttendanceStatistics(
                totalClasses,
                totalPresent,
                totalAbsent,
                totalLate,
                totalLeave,
                attendancePercentage
        );
    }

}