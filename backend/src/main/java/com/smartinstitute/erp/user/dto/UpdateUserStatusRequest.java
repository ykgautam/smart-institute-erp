package com.smartinstitute.erp.user.dto;

import com.smartinstitute.erp.common.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserStatusRequest {

    @NotNull(message = "Status is required.")
    private UserStatus status;

}