package com.smartinstitute.erp.institute.dto;

import com.smartinstitute.erp.common.enums.InstituteStatus;
import com.smartinstitute.erp.common.enums.InstituteType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InstituteResponse {

    private Long id;

    private String name;

    private String email;

    private String mobile;

    private String landline;

    private String address;

    private String city;

    private String state;

    private String pincode;

    private String gstNumber;

    private String website;

    private String logoPath;

    private InstituteType type;

    private InstituteStatus status;

    private Boolean active;

    private Long ownerId;

}