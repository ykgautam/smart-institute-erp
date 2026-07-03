package com.smartinstitute.erp.fee.dto.request;

import com.smartinstitute.erp.common.enums.fee.PaymentMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollectFeeRequest {

    @NotNull
    private Long studentFeeId;

    @NotNull
    @Positive
    private Double amount;

    @NotNull
    private PaymentMode paymentMode;

    @Size(max = 100)
    private String transactionReference;

    @Size(max = 300)
    private String remarks;

}