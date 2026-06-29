package com.smartinstitute.erp.common.service;

import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseCrudService {

    protected final SecurityUtil securityUtil;

    protected final InstituteAccessValidator instituteAccessValidator;

    protected Institute getCurrentInstitute() {
        return securityUtil.getCurrentInstitute();
    }

    protected Long getCurrentInstituteId() {
        return securityUtil.getCurrentInstituteId();
    }

    protected void validateOwnership(
            Long entityInstituteId,
            String resourceName) {

        instituteAccessValidator.validateOwnership(
                entityInstituteId,
                getCurrentInstitute(),
                resourceName
        );
    }
}