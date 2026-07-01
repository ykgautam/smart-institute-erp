package com.smartinstitute.erp.batch.dto.request;

import com.smartinstitute.erp.common.enums.BatchStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchStatusRequest {

    @NotNull(message = "Status is required.")
    private BatchStatus status;

}