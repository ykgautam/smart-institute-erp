package com.smartinstitute.erp.test.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.test.dto.request.CreateQuestionRequest;
import com.smartinstitute.erp.test.dto.request.QuestionSearchRequest;
import com.smartinstitute.erp.test.dto.request.QuestionStatusRequest;
import com.smartinstitute.erp.test.dto.request.UpdateQuestionRequest;
import com.smartinstitute.erp.test.dto.response.QuestionResponse;
import com.smartinstitute.erp.test.service.QuestionService;
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
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
@Tag(name = "Question Management", description = "Question Bank Management APIs")
@SecurityRequirement(name = "bearerAuth")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Create Question")
    public ResponseEntity<ApiResponse<QuestionResponse>> createQuestion(
            @Valid @RequestBody CreateQuestionRequest request) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        questionService.createQuestion(request),
                        "Question created successfully."
                )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Update Question")
    public ResponseEntity<ApiResponse<QuestionResponse>> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody UpdateQuestionRequest request) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        questionService.updateQuestion(id, request),
                        "Question updated successfully."
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get Question By Id")
    public ResponseEntity<ApiResponse<QuestionResponse>> getQuestionById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        questionService.getQuestionById(id),
                        "Question fetched successfully."
                )
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get All Questions")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getAllQuestions() {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        questionService.getAllQuestions(),
                        "Questions fetched successfully."
                )
        );
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get Questions By Course")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getQuestionsByCourse(
            @PathVariable Long courseId) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        questionService.getQuestionsByCourse(courseId),
                        "Questions fetched successfully."
                )
        );
    }

    @GetMapping("/topic/{topicId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get Questions By Topic")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getQuestionsByTopic(
            @PathVariable Long topicId) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        questionService.getQuestionsByTopic(topicId),
                        "Questions fetched successfully."
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Delete Question")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(
            @PathVariable Long id) {

        questionService.deleteQuestion(id);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        null,
                        "Question deleted successfully."
                )
        );
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Search Questions")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> searchQuestions(
            @ModelAttribute QuestionSearchRequest request) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        questionService.searchQuestions(request),
                        "Questions fetched successfully."
                )
        );

    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Update Question Status")
    public ResponseEntity<ApiResponse<Void>> updateQuestionStatus(
            @PathVariable Long id,
            @Valid @RequestBody QuestionStatusRequest request) {

        questionService.updateQuestionStatus(id, request);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        null,
                        "Question status updated successfully."
                )
        );
    }
    @PostMapping("/{id}/duplicate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Duplicate Question")
    public ResponseEntity<ApiResponse<QuestionResponse>> duplicateQuestion(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        questionService.duplicateQuestion(id),
                        "Question duplicated successfully."
                )
        );

    }
}