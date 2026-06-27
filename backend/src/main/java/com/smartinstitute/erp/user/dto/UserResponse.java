package com.smartinstitute.erp.user.dto;

import com.smartinstitute.erp.common.enums.Gender;
import com.smartinstitute.erp.common.enums.RoleType;
import com.smartinstitute.erp.common.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String mobile;

    private Gender gender;

    private UserStatus status;

    private RoleType role;

}