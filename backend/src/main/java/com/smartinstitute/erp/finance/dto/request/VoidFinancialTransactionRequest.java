package com.smartinstitute.erp.finance.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VoidFinancialTransactionRequest {

    @NotBlank(message = "Void reason is required.")
    @Size(max = 500, message = "Void reason cannot exceed 500 characters.")
    private String reason;
}