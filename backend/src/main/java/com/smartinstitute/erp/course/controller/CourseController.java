package com.smartinstitute.erp.course.controller;

import com.smartinstitute.erp.common.pagination.PageResponse;
import com.smartinstitute.erp.common.pagination.PaginationRequest;
import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.common.enums.SortDirection;
import com.smartinstitute.erp.course.dto.request.CourseStatusRequest;
import com.smartinstitute.erp.course.dto.request.CreateCourseRequest;
import com.smartinstitute.erp.course.dto.request.UpdateCourseRequest;
import com.smartinstitute.erp.course.dto.response.CourseResponse;
import com.smartinstitute.erp.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Tag(name = "Course Management", description = "Course Management APIs")
@SecurityRequirement(name = "bearerAuth")
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Create Course")
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(
            @Valid @RequestBody CreateCourseRequest request) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        courseService.createCourse(request),
                        "Course created successfully."
                )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Update Course")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCourseRequest request) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        courseService.updateCourse(id, request),
                        "Course updated successfully."
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get Course By Id")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourseById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        courseService.getCourseById(id),
                        "Course fetched successfully."
                )
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<CourseResponse>>> getCourses(
            @Valid @ModelAttribute PaginationRequest request) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        courseService.getCourses(request),
                        "Courses fetched successfully."
                )
        );
    }

//    @GetMapping
//    @PreAuthorize("hasRole('SUPER_ADMIN')")
//    @Operation(summary = "Get All Courses")
//    public ResponseEntity<ApiResponse<PageResponse<CourseResponse>>> getCourses(
//
//            @Parameter(description = "Page number")
//            @RequestParam(defaultValue = "0")
//            int page,
//
//            @Parameter(description = "Page size")
//            @RequestParam(defaultValue = "10")
//            int size,
//
//            @Parameter(description = "Sort field")
//            @RequestParam(defaultValue = "id")
//            String sortBy,
//
//            @Parameter(description = "Sort direction")
//            @RequestParam(defaultValue = "ASC")
//            SortDirection direction,
//
//            @Parameter(description = "Search keyword")
//            @RequestParam(required = false)
//            String keyword) {
//
//        PaginationRequest request = new PaginationRequest();
//
//        request.setPage(page);
//        request.setSize(size);
//        request.setSortBy(sortBy);
//        request.setDirection(direction);
//        request.setKeyword(keyword);
//
//        return ResponseEntity.ok(
//                ApiResponseUtil.success(
//                        courseService.getCourses(request),
//                        "Courses fetched successfully."
//                )
//        );
//    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Update Course Status")
    public ResponseEntity<ApiResponse<Void>> updateCourseStatus(
            @PathVariable Long id,
            @Valid @RequestBody CourseStatusRequest request) {

        courseService.updateCourseStatus(id, request);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        null,
                        "Course status updated successfully."
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Delete Course")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(
            @PathVariable Long id) {

        courseService.deleteCourse(id);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        null,
                        "Course deleted successfully."
                )
        );
    }

}