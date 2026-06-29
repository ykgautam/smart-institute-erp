package com.smartinstitute.erp.common.validation;

import com.smartinstitute.erp.common.exception.ResourceNotFoundException;
import com.smartinstitute.erp.institute.entity.Institute;
import org.springframework.stereotype.Component;

@Component
public class InstituteAccessValidator {

    public void validateOwnership(
            Long entityInstituteId,
            Institute currentInstitute,
            String resourceName) {

        if (!entityInstituteId.equals(currentInstitute.getId())) {

            throw new ResourceNotFoundException(
                    resourceName + " not found."
            );
        }
    }
}