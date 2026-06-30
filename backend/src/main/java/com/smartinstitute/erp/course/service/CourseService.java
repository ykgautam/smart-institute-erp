package com.smartinstitute.erp.course.service;

import com.smartinstitute.erp.common.pagination.PageResponse;
import com.smartinstitute.erp.common.pagination.PaginationRequest;
import com.smartinstitute.erp.course.dto.request.CourseStatusRequest;
import com.smartinstitute.erp.course.dto.request.CreateCourseRequest;
import com.smartinstitute.erp.course.dto.request.UpdateCourseRequest;
import com.smartinstitute.erp.course.dto.response.CourseResponse;

public interface CourseService {

    /**
     * Creates a new course.
     */
    CourseResponse createCourse(CreateCourseRequest request);

    /**
     * Updates an existing course.
     */
    CourseResponse updateCourse(
            Long courseId,
            UpdateCourseRequest request
    );

    /**
     * Returns course by id.
     */
    CourseResponse getCourseById(Long courseId);

    /**
     * Returns paginated list of courses.
     */
    PageResponse<CourseResponse> getCourses(
            PaginationRequest request
    );

    /**
     * Updates course status.
     */
    void updateCourseStatus(
            Long courseId,
            CourseStatusRequest request
    );

    /**
     * Soft deletes a course.
     */
    void deleteCourse(Long courseId);

}