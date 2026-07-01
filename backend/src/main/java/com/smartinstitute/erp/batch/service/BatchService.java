package com.smartinstitute.erp.batch.service;

import com.smartinstitute.erp.batch.dto.request.BatchStatusRequest;
import com.smartinstitute.erp.batch.dto.request.CreateBatchRequest;
import com.smartinstitute.erp.batch.dto.request.UpdateBatchRequest;
import com.smartinstitute.erp.batch.dto.response.BatchResponse;
import com.smartinstitute.erp.common.pagination.PageResponse;
import com.smartinstitute.erp.common.pagination.PaginationRequest;

public interface BatchService {

    BatchResponse createBatch(CreateBatchRequest request);

    BatchResponse updateBatch(Long batchId, UpdateBatchRequest request);

    BatchResponse getBatchById(Long batchId);

    PageResponse<BatchResponse> getBatches(PaginationRequest request);

    void updateBatchStatus(Long batchId, BatchStatusRequest request);

    void deleteBatch(Long batchId);

}