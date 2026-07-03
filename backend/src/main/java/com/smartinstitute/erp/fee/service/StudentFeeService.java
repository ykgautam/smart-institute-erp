package com.smartinstitute.erp.fee.service;

import com.smartinstitute.erp.fee.dto.request.AssignStudentFeeRequest;
import com.smartinstitute.erp.fee.dto.response.StudentFeeResponse;

import java.util.List;

public interface StudentFeeService {

    StudentFeeResponse assignFee(AssignStudentFeeRequest request);

    StudentFeeResponse getStudentFee(Long studentId);

    List<StudentFeeResponse> getAllStudentFees();

}