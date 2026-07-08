package com.smartinstitute.erp.test.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.test.dto.request.AddQuestionToTestRequest;
import com.smartinstitute.erp.test.dto.request.UpdateTestQuestionOrderRequest;
import com.smartinstitute.erp.test.dto.response.TestQuestionResponse;
import com.smartinstitute.erp.test.service.TestQuestionService;
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
@RequestMapping("/api/v1/tests")
@RequiredArgsConstructor
@Tag(
        name = "Test Question Management",
        description = "Manage Questions assigned to Tests"
)
@SecurityRequirement(name = "bearerAuth")
public class TestQuestionController {

    private final TestQuestionService testQuestionService;

    @PostMapping("/{testId}/questions")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Add Questions To Test")
    public ResponseEntity<ApiResponse<Void>> addQuestionsToTest(
            @PathVariable Long testId,
            @Valid @RequestBody AddQuestionToTestRequest request) {

        testQuestionService.addQuestionsToTest(
                testId,
                request
        );

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        null,
                        "Questions added successfully."
                )
        );
    }

    @GetMapping("/{testId}/questions")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get Questions By Test")
    public ResponseEntity<ApiResponse<List<TestQuestionResponse>>> getQuestionsByTest(
            @PathVariable Long testId) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        testQuestionService.getQuestionsByTest(testId),
                        "Questions fetched successfully."
                )
        );
    }

    @DeleteMapping("/{testId}/questions/{questionId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Remove Question From Test")
    public ResponseEntity<ApiResponse<Void>> removeQuestionFromTest(
            @PathVariable Long testId,
            @PathVariable Long questionId) {

        testQuestionService.removeQuestionFromTest(
                testId,
                questionId
        );

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        null,
                        "Question removed successfully."
                )
        );
    }

    @PutMapping("/{testId}/questions/order")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Update Question Order")
    public ResponseEntity<ApiResponse<Void>> updateQuestionOrder(
            @PathVariable Long testId,
            @Valid @RequestBody List<UpdateTestQuestionOrderRequest> request) {

        testQuestionService.updateQuestionOrder(
                testId,
                request
        );

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        null,
                        "Question order updated successfully."
                )
        );
    }

}