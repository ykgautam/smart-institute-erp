package com.smartinstitute.erp.fee.repository;

import com.smartinstitute.erp.fee.entity.FeePayment;
import com.smartinstitute.erp.fee.entity.StudentFee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeePaymentRepository
        extends JpaRepository<FeePayment, Long> {

    List<FeePayment> findByStudentFeeOrderByPaymentDateDesc(
            StudentFee studentFee
    );

    List<FeePayment> findByStudentFeeAndActiveTrue(
            StudentFee studentFee
    );
}