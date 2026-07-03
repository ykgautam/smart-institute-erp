package com.smartinstitute.erp.fee.dto.response;

import com.smartinstitute.erp.common.enums.fee.PaymentMode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class FeePaymentResponse {

    private Long id;

    private Double amount;

    private LocalDate paymentDate;

    private PaymentMode paymentMode;

    private String transactionReference;

    private String receiptNumber;

    private String remarks;

}