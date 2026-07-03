package com.smartinstitute.erp.fee.service;

import com.smartinstitute.erp.fee.dto.request.CreateFeeStructureRequest;
import com.smartinstitute.erp.fee.dto.request.UpdateFeeStructureRequest;
import com.smartinstitute.erp.fee.dto.response.FeeStructureResponse;

import java.util.List;

public interface FeeStructureService {

    FeeStructureResponse createFeeStructure(CreateFeeStructureRequest request);

    FeeStructureResponse updateFeeStructure(Long id, UpdateFeeStructureRequest request);

    FeeStructureResponse getFeeStructure(Long id);

    List<FeeStructureResponse> getAllFeeStructures();

    void deleteFeeStructure(Long id);

}