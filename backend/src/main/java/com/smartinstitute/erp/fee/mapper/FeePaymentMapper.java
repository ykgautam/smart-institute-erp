package com.smartinstitute.erp.fee.mapper;

import com.smartinstitute.erp.fee.dto.response.FeePaymentResponse;
import com.smartinstitute.erp.fee.entity.FeePayment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FeePaymentMapper {

    FeePaymentResponse toResponse(FeePayment entity);

}