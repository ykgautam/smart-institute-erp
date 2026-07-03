package com.smartinstitute.erp.fee.dto.request;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Setter
public class AssignStudentFeeRequest {

    @NotNull
    private Long studentId;

    @NotNull
    private Long feeStructureId;

    @PositiveOrZero
    private BigDecimal discount;

}