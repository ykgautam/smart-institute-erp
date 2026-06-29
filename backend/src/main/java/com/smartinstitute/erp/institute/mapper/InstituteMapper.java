package com.smartinstitute.erp.institute.mapper;

import com.smartinstitute.erp.common.enums.InstituteStatus;
import com.smartinstitute.erp.institute.dto.CreateInstituteRequest;
import com.smartinstitute.erp.institute.dto.InstituteResponse;
import com.smartinstitute.erp.institute.dto.UpdateInstituteRequest;
import com.smartinstitute.erp.institute.entity.Institute;
import org.springframework.stereotype.Component;

@Component
public class InstituteMapper {

    public Institute toEntity(CreateInstituteRequest request) {

        Institute institute = new Institute();

        institute.setName(request.getName());
        institute.setEmail(request.getEmail());
        institute.setMobile(request.getMobile());
        institute.setLandline(request.getLandline());
        institute.setAddress(request.getAddress());
        institute.setCity(request.getCity());
        institute.setState(request.getState());
        institute.setPincode(request.getPincode());
        institute.setGstNumber(request.getGstNumber());
        institute.setWebsite(request.getWebsite());

        institute.setType(request.getType());
        institute.setStatus(InstituteStatus.ACTIVE);
        institute.setActive(true);

        return institute;
    }

    public void updateEntity(
            Institute institute,
            UpdateInstituteRequest request) {

        institute.setName(request.getName());
        institute.setEmail(request.getEmail());
        institute.setMobile(request.getMobile());
        institute.setLandline(request.getLandline());
        institute.setAddress(request.getAddress());
        institute.setCity(request.getCity());
        institute.setState(request.getState());
        institute.setPincode(request.getPincode());
        institute.setGstNumber(request.getGstNumber());
        institute.setWebsite(request.getWebsite());

        if (request.getType() != null) {
            institute.setType(request.getType());
        }
    }

    public InstituteResponse toResponse(Institute institute) {

        return InstituteResponse.builder()
                .id(institute.getId())
                .name(institute.getName())
                .email(institute.getEmail())
                .mobile(institute.getMobile())
                .landline(institute.getLandline())
                .address(institute.getAddress())
                .city(institute.getCity())
                .state(institute.getState())
                .pincode(institute.getPincode())
                .gstNumber(institute.getGstNumber())
                .website(institute.getWebsite())
                .logoPath(institute.getLogoPath())
                .type(institute.getType())
                .status(institute.getStatus())
                .active(institute.getActive())
                .ownerId(
                        institute.getOwner() != null
                                ? institute.getOwner().getId()
                                : null
                )
                .build();
    }

}