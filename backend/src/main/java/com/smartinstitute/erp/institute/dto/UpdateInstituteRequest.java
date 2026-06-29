package com.smartinstitute.erp.institute.dto;

import com.smartinstitute.erp.common.enums.InstituteType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateInstituteRequest {

    @Size(max = 150)
    private String name;

    @Email
    @Size(max = 100)
    private String email;

    @Pattern(regexp = "^[0-9]{10}$")
    private String mobile;

    @Size(max = 20)
    private String landline;

    @Size(max = 300)
    private String address;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String state;

    @Size(max = 10)
    private String pincode;

    @Size(max = 30)
    private String gstNumber;

    @Size(max = 150)
    private String website;

    private InstituteType type;

}