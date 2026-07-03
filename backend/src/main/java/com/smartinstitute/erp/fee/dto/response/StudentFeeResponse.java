package com.smartinstitute.erp.fee.dto.response;

import com.smartinstitute.erp.common.enums.fee.FeeStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StudentFeeResponse {

    private Long id;

    private Long studentId;

    private String studentName;

    private Double totalFee;

    private Double discount;

    private Double finalFee;

    private Double paidAmount;

    private Double pendingAmount;

    private FeeStatus status;

}