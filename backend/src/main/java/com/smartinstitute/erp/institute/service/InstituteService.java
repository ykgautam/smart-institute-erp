package com.smartinstitute.erp.institute.service;

import com.smartinstitute.erp.institute.dto.*;

import java.util.List;

public interface InstituteService {

    InstituteResponse createInstitute(CreateInstituteRequest request);

    InstituteResponse getInstituteById(Long id);

    List<InstituteResponse> getAllInstitutes();

    InstituteResponse updateInstitute(
            Long id,
            UpdateInstituteRequest request);

    void deleteInstitute(Long id);

    InstituteResponse updateInstituteStatus(
            Long id,
            UpdateInstituteStatusRequest request);

    InstituteOnboardingResponse onboardInstitute(
            InstituteOnboardingRequest request);
}