package com.smartinstitute.erp.student.service;

import com.smartinstitute.erp.student.dto.CreateStudentRequest;
import com.smartinstitute.erp.student.dto.StudentResponse;
import com.smartinstitute.erp.student.dto.StudentStatusRequest;
import com.smartinstitute.erp.student.dto.UpdateStudentRequest;

import java.util.List;

public interface StudentService {

    StudentResponse createStudent(CreateStudentRequest request);

    StudentResponse updateStudent(Long id, UpdateStudentRequest request);

    StudentResponse getStudentById(Long id);

    List<StudentResponse> getAllStudents();

    List<StudentResponse> searchStudents(String keyword);

    StudentResponse updateStatus(Long id, StudentStatusRequest request);

    void deleteStudent(Long id);

}