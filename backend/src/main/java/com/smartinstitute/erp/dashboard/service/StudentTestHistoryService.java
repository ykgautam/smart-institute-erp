package com.smartinstitute.erp.dashboard.service;

import com.smartinstitute.erp.common.enums.test.StudentTestStatus;
import com.smartinstitute.erp.dashboard.dto.response.StudentTestHistoryResponse;
import com.smartinstitute.erp.student.entity.Student;
import com.smartinstitute.erp.test.entity.StudentTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface StudentTestHistoryService {

    Page<StudentTestHistoryResponse> getHistory(

            int page,

            int size,

            StudentTestStatus status,

            LocalDate fromDate,

            LocalDate toDate,

            String search,

            String sort,

            String direction

    );

    Page<StudentTest> findByStudentOrderBySubmittedAtDesc(
            Student student,
            Pageable pageable
    );
}