package com.smartinstitute.erp.batch.controller;

import com.smartinstitute.erp.batch.service.BatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.batch.dto.request.CreateBatchRequest;
import com.smartinstitute.erp.batch.dto.request.UpdateBatchRequest;
import com.smartinstitute.erp.batch.dto.response.BatchResponse;
import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.enums.SortDirection;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.smartinstitute.erp.batch.dto.request.BatchStatusRequest;
import com.smartinstitute.erp.common.pagination.PageResponse;
import com.smartinstitute.erp.common.pagination.PaginationRequest;
import org.springframework.data.domain.Sort;

import static com.smartinstitute.erp.batch.constant.BatchApiConstants.*;

import static com.smartinstitute.erp.batch.constant.BatchApiConstants.BASE_URL;

@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;

    @PostMapping
    public ResponseEntity<ApiResponse<BatchResponse>> createBatch(
            @Valid @RequestBody
            CreateBatchRequest request) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        batchService.createBatch(request),
                        "Batch created successfully."
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BatchResponse>> updateBatch(
            @PathVariable Long id,

            @Valid
            @RequestBody
            UpdateBatchRequest request) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        batchService.updateBatch(
                                id,
                                request
                        ),
                        "Batch updated successfully."
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BatchResponse>> getBatchById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        batchService.getBatchById(id),
                        "Batch fetched successfully."
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<BatchResponse>>> getBatches(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "id")
            String sortBy,

            @RequestParam(defaultValue = "ASC")
            SortDirection direction,

            @RequestParam(required = false)
            String keyword) {

        PaginationRequest request = new PaginationRequest();

        request.setPage(page);
        request.setSize(size);
        request.setSortBy(sortBy);
        request.setDirection(direction);
        request.setKeyword(keyword);

        return ResponseEntity.ok(

                ApiResponseUtil.success(

                        batchService.getBatches(request),

                        "Batches fetched successfully."
                )
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateBatchStatus(
            @PathVariable Long id,
            @Valid
            @RequestBody BatchStatusRequest request) {

        batchService.updateBatchStatus(id, request);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        null,
                        "Batch status updated successfully."
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBatch(
            @PathVariable Long id) {

        batchService.deleteBatch(id);

        return ResponseEntity.ok(

                ApiResponseUtil.success(

                        null,

                        "Batch deleted successfully."
                )
        );
    }
}