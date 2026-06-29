package com.smartinstitute.erp.student.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.student.dto.CreateStudentRequest;
import com.smartinstitute.erp.student.dto.StudentResponse;
import com.smartinstitute.erp.student.dto.StudentStatusRequest;
import com.smartinstitute.erp.student.dto.UpdateStudentRequest;
import com.smartinstitute.erp.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.smartinstitute.erp.common.enums.SortDirection;
import com.smartinstitute.erp.common.pagination.PageResponse;
import com.smartinstitute.erp.common.pagination.PaginationRequest;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;

@Tag(
        name = "Student Management",
        description = "APIs for managing students"
)
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','INSTITUTE_ADMIN')")
    @Operation(
            summary = "Create Student",
            description = "Creates a new student in the logged-in institute.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Student created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Duplicate admission number/email/mobile")
    })
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(
            @Valid @RequestBody CreateStudentRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponseUtil.success(
                                studentService.createStudent(request),
                                "Student created successfully."
                        )
                );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','INSTITUTE_ADMIN')")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStudentRequest request) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        studentService.updateStudent(id, request),
                        "Student updated successfully."
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','INSTITUTE_ADMIN','STAFF','FACULTY')")
    @Operation(
            summary = "Get Student",
            description = "Fetch student by id.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Success"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Student not found")
    })
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        studentService.getStudentById(id),
                        "Student fetched successfully."
                )
        );
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','INSTITUTE_ADMIN','STAFF','FACULTY')")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getAllStudents() {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        studentService.getAllStudents1(),
                        "Students fetched successfully."
                )
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','INSTITUTE_ADMIN','STAFF','FACULTY')")
    @Operation(
            summary = "Get Students",
            description = "Returns paginated students of the logged-in institute.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<ApiResponse<PageResponse<StudentResponse>>> getStudents(
            @Valid @ModelAttribute PaginationRequest request) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        studentService.getStudents(request),
                        "Students fetched successfully."
                )
        );
    }

//    @GetMapping
//    @PreAuthorize("hasAnyRole('SUPER_ADMIN','INSTITUTE_ADMIN','STAFF','FACULTY')")
//    @Operation(
//            summary = "Get Students",
//            description = "Returns paginated students of the logged-in institute.",
//            security = @SecurityRequirement(name = "Bearer Authentication")
//    )
//    public ResponseEntity<ApiResponse<PageResponse<StudentResponse>>> getStudents(
//
//            @RequestParam(defaultValue = "0")
//            @Parameter(description = "Page number", example = "0")
//            int page,
//
//            @RequestParam(defaultValue = "10")
//            @Parameter(description = "Page size", example = "10")
//            int size,
//
//            @RequestParam(defaultValue = "id")
//            @Parameter(description = "Sort field", example = "firstName")
//            String sortBy,
//
//            @RequestParam(defaultValue = "ASC")
//            @Parameter(description = "Sort direction", example = "ASC")
//            SortDirection direction,
//
//            @RequestParam(required = false)
//            @Parameter(description = "Search keyword", example = "Rahul")
//            String keyword) {
//
//        PaginationRequest request = new PaginationRequest();
//        request.setPage(page);
//        request.setSize(size);
//        request.setSortBy(sortBy);
//        request.setDirection(direction);
//        request.setKeyword(keyword);
//
//        return ResponseEntity.ok(
//                ApiResponseUtil.success(
//                        studentService.getStudents(request),
//                        "Students fetched successfully."
//                )
//        );
//    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','INSTITUTE_ADMIN','STAFF','FACULTY')")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> searchStudents(
            @RequestParam String keyword) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        studentService.searchStudents(keyword),
                        "Students fetched successfully."
                )
        );
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','INSTITUTE_ADMIN')")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StudentStatusRequest request) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        studentService.updateStatus(id, request),
                        "Student status updated successfully."
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','INSTITUTE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(
            @PathVariable Long id) {

        studentService.deleteStudent(id);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        null,
                        "Student deleted successfully."
                )
        );
    }

}