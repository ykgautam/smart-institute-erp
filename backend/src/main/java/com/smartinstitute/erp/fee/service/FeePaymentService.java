package com.smartinstitute.erp.fee.service;

import com.smartinstitute.erp.fee.dto.request.CollectFeeRequest;
import com.smartinstitute.erp.fee.dto.response.FeePaymentResponse;

import java.util.List;

public interface FeePaymentService {

    /**
     * Collect fee from student.
     *
     * @param request fee payment request
     * @return payment details
     */
    FeePaymentResponse collectFee(CollectFeeRequest request);

    /**
     * Get payment history for a Student Fee.
     *
     * @param studentFeeId Student Fee ID
     * @return list of payments
     */
    List<FeePaymentResponse> getPaymentHistory(Long studentFeeId);

}