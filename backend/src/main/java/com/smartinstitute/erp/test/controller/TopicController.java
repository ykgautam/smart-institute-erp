package com.smartinstitute.erp.test.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.test.dto.request.CreateTopicRequest;
import com.smartinstitute.erp.test.dto.request.UpdateTopicRequest;
import com.smartinstitute.erp.test.dto.response.TopicResponse;
import com.smartinstitute.erp.test.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/topics")
@RequiredArgsConstructor
@Tag(name = "Topic Management", description = "Topic Management APIs")
@SecurityRequirement(name = "bearerAuth")
public class TopicController {

    private final TopicService topicService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Create Topic")
    public ResponseEntity<ApiResponse<TopicResponse>> createTopic(
            @Valid @RequestBody CreateTopicRequest request) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        topicService.createTopic(request),
                        "Topic created successfully."
                )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Update Topic")
    public ResponseEntity<ApiResponse<TopicResponse>> updateTopic(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTopicRequest request) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        topicService.updateTopic(id, request),
                        "Topic updated successfully."
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get Topic By Id")
    public ResponseEntity<ApiResponse<TopicResponse>> getTopicById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        topicService.getTopicById(id),
                        "Topic fetched successfully."
                )
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get All Topics")
    public ResponseEntity<ApiResponse<List<TopicResponse>>> getAllTopics() {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        topicService.getAllTopics(),
                        "Topics fetched successfully."
                )
        );
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get Topics By Course")
    public ResponseEntity<ApiResponse<List<TopicResponse>>> getTopicsByCourse(
            @PathVariable Long courseId) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        topicService.getTopicsByCourse(courseId),
                        "Course topics fetched successfully."
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Delete Topic")
    public ResponseEntity<ApiResponse<Void>> deleteTopic(
            @PathVariable Long id) {

        topicService.deleteTopic(id);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        null,
                        "Topic deleted successfully."
                )
        );
    }

}