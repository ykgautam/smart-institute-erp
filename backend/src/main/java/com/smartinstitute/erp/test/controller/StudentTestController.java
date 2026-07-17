package com.smartinstitute.erp.test.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.test.dto.request.SaveAnswerRequest;
import com.smartinstitute.erp.test.dto.request.StartTestRequest;
import com.smartinstitute.erp.test.dto.response.*;
import com.smartinstitute.erp.test.service.StudentTestService;
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
@RequestMapping("/api/v1/student-tests")
@RequiredArgsConstructor
@Tag(
        name = "Student Test",
        description = "Student Test Attempt APIs"
)
@SecurityRequirement(name = "bearerAuth")
public class StudentTestController {

    private final StudentTestService studentTestService;

    @PostMapping("/start")
    @PreAuthorize("hasAnyRole('STUDENT','SUPER_ADMIN')")
    @Operation(summary = "Start Test")
    public ResponseEntity<ApiResponse<StudentTestResponse>> startTest(
            @Valid @RequestBody StartTestRequest request) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        studentTestService.startTest(request),
                        "Test started successfully."
                )
        );
    }

    @GetMapping("/{studentTestId}")
    @PreAuthorize("hasAnyRole('STUDENT','SUPER_ADMIN')")
    @Operation(summary = "Get Student Test")
    public ResponseEntity<ApiResponse<StudentTestResponse>> getStudentTest(
            @PathVariable Long studentTestId) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        studentTestService.getStudentTest(studentTestId),
                        "Student test fetched successfully."
                )
        );
    }

    @GetMapping("/my-tests")
    @PreAuthorize("hasAnyRole('STUDENT','SUPER_ADMIN)")
    @Operation(summary = "Get My Test History")
    public ResponseEntity<ApiResponse<List<StudentTestSummaryResponse>>> getMyTests() {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        studentTestService.getMyTests(),
                        "Student test history fetched successfully."
                )
        );
    }

    @GetMapping("/{studentTestId}/questions")
    @PreAuthorize("hasAnyRole('STUDENT','SUPER_ADMIN')")
    @Operation(summary = "Get All Questions For Student Test")
    public ResponseEntity<ApiResponse<List<QuestionForStudentResponse>>> getQuestions(
            @PathVariable Long studentTestId) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        studentTestService.getQuestions(studentTestId),
                        "Questions fetched successfully."
                )
        );
    }

    @GetMapping("/{studentTestId}/questions/{questionId}")
    @PreAuthorize("hasAnyRole('STUDENT','SUPER_ADMIN')")
    @Operation(summary = "Get Single Question")
    public ResponseEntity<ApiResponse<QuestionForStudentResponse>> getQuestion(
            @PathVariable Long studentTestId,
            @PathVariable Long questionId) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        studentTestService.getQuestion(
                                studentTestId,
                                questionId
                        ),
                        "Question fetched successfully."
                )
        );
    }

    @PostMapping("/{studentTestId}/answers")
    @PreAuthorize("hasAnyRole('STUDENT','SUPER_ADMIN')")
    @Operation(summary = "Save Student Answer")
    public ResponseEntity<ApiResponse<StudentAnswerResponse>> saveAnswer(
            @PathVariable Long studentTestId,
            @Valid @RequestBody SaveAnswerRequest request) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        studentTestService.saveAnswer(
                                studentTestId,
                                request
                        ),
                        "Answer saved successfully."
                )
        );
    }

    @GetMapping("/{studentTestId}/answers")
    @PreAuthorize("hasAnyRole('STUDENT','SUPER_ADMIN')")
    @Operation(summary = "Get Saved Answers")
    public ResponseEntity<ApiResponse<List<StudentAnswerResponse>>> getSavedAnswers(
            @PathVariable Long studentTestId) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        studentTestService.getSavedAnswers(studentTestId),
                        "Saved answers fetched successfully."
                )
        );
    }

    @PostMapping("/{studentTestId}/submit")
    @PreAuthorize("hasAnyRole('STUDENT','SUPER_ADMIN')")
    @Operation(summary = "Submit Student Test")
    public ResponseEntity<ApiResponse<Void>> submitTest(
            @PathVariable Long studentTestId) {

        studentTestService.submitTest(studentTestId);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        null,
                        "Test submitted successfully."
                )
        );
    }

    @GetMapping("/{studentTestId}/result")
    @PreAuthorize("hasAnyRole('STUDENT','SUPER_ADMIN')")
    @Operation(summary = "Get Student Test Result")
    public ResponseEntity<ApiResponse<StudentTestResultResponse>> getResult(
            @PathVariable Long studentTestId) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        studentTestService.getResult(studentTestId),
                        "Result fetched successfully."
                )
        );
    }
}