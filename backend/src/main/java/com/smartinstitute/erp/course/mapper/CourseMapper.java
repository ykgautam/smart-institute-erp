package com.smartinstitute.erp.course.mapper;

import com.smartinstitute.erp.course.dto.request.CreateCourseRequest;
import com.smartinstitute.erp.course.dto.request.UpdateCourseRequest;
import com.smartinstitute.erp.course.dto.response.CourseResponse;
import com.smartinstitute.erp.course.entity.Course;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {

    /**
     * Converts CreateCourseRequest to Course entity.
     */
    public Course toEntity(CreateCourseRequest request) {

        if (request == null) {
            return null;
        }

        Course course = new Course();

        course.setCourseCode(request.getCourseCode());
        course.setCourseName(request.getCourseName());
        course.setDescription(request.getDescription());
        course.setDuration(request.getDuration());
        course.setDurationType(request.getDurationType());
        course.setFee(request.getFee());

        return course;
    }

    /**
     * Updates existing Course entity.
     */
    public void updateEntity(
            Course course,
            UpdateCourseRequest request) {

        if (course == null || request == null) {
            return;
        }

        course.setCourseCode(request.getCourseCode().trim().toUpperCase());
        course.setCourseName(request.getCourseName());
        course.setDescription(request.getDescription());
        course.setDuration(request.getDuration());
        course.setDurationType(request.getDurationType());
        course.setFee(request.getFee());
    }

    /**
     * Converts Course entity to CourseResponse.
     */
    public CourseResponse toResponse(Course course) {

        if (course == null) {
            return null;
        }

        CourseResponse response = new CourseResponse();

        response.setId(course.getId());
        response.setCourseCode(course.getCourseCode());
        response.setCourseName(course.getCourseName());
        response.setDescription(course.getDescription());
        response.setDuration(course.getDuration());
        response.setDurationType(course.getDurationType());
        response.setFee(course.getFee());
        response.setStatus(course.getStatus());
        response.setActive(course.getActive());

        return response;
    }

}