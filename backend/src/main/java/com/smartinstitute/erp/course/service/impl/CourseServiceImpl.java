package com.smartinstitute.erp.course.service.impl;

import com.smartinstitute.erp.common.exception.DuplicateResourceException;
import com.smartinstitute.erp.common.exception.InvalidRequestException;
import com.smartinstitute.erp.common.exception.ResourceNotFoundException;
import com.smartinstitute.erp.course.constant.CourseSortableFields;
import com.smartinstitute.erp.course.dto.request.CreateCourseRequest;
import com.smartinstitute.erp.course.dto.request.UpdateCourseRequest;
import com.smartinstitute.erp.course.dto.response.CourseResponse;
import com.smartinstitute.erp.course.entity.Course;
import com.smartinstitute.erp.course.mapper.CourseMapper;
import com.smartinstitute.erp.course.repository.CourseRepository;
import com.smartinstitute.erp.course.service.CourseService;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.smartinstitute.erp.common.pagination.PageResponse;
import com.smartinstitute.erp.common.pagination.PaginationRequest;
import com.smartinstitute.erp.common.pagination.PaginationUtils;
import com.smartinstitute.erp.course.dto.request.CourseStatusRequest;
import com.smartinstitute.erp.course.specification.CourseSpecification;
import com.smartinstitute.erp.common.specification.SpecificationBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    private final CourseMapper courseMapper;

    private final SecurityUtil securityUtil;

    @Override
    public CourseResponse createCourse(
            CreateCourseRequest request) {

        Institute institute = securityUtil.getCurrentInstitute();

        normalizeRequest(request);

        validateDuplicateCourseCode(
                institute,
                request.getCourseCode()
        );

        validateDuplicateCourseName(
                institute,
                request.getCourseName()
        );

        Course course = courseMapper.toEntity(request);

        course.setInstitute(institute);

        Course savedCourse = courseRepository.save(course);

        return courseMapper.toResponse(savedCourse);
    }

    @Override
    public CourseResponse updateCourse(
            Long courseId,
            UpdateCourseRequest request) {

        Course course = getCourseEntity(courseId);

        normalizeRequest(request);

        validateDuplicateCourseCode(
                course,
                request.getCourseCode()
        );

        validateDuplicateCourseName(
                course,
                request.getCourseName()
        );

        courseMapper.updateEntity(course, request);

        Course updatedCourse =
                courseRepository.save(course);

        return courseMapper.toResponse(updatedCourse);
    }

    private void normalizeRequest(
            CreateCourseRequest request) {

        request.setCourseCode(
                request.getCourseCode()
                        .trim()
                        .toUpperCase()
        );

        request.setCourseName(
                request.getCourseName()
                        .trim()
        );
    }

    private void normalizeRequest(
            UpdateCourseRequest request) {

        request.setCourseCode(
                request.getCourseCode()
                        .trim()
                        .toUpperCase()
        );

        request.setCourseName(
                request.getCourseName()
                        .trim()
        );
    }

    private void validateDuplicateCourseCode(
            Institute institute,
            String courseCode) {

        if (courseRepository
                .existsByInstituteAndCourseCodeIgnoreCase(
                        institute,
                        courseCode)) {

            throw new DuplicateResourceException(
                    "Course code already exists."
            );
        }
    }

    private void validateDuplicateCourseName(
            Institute institute,
            String courseName) {

        if (courseRepository
                .existsByInstituteAndCourseNameIgnoreCase(
                        institute,
                        courseName)) {

            throw new DuplicateResourceException(
                    "Course name already exists."
            );
        }
    }

    private void validateDuplicateCourseCode(
            Course course,
            String courseCode) {

        boolean exists =
                courseRepository
                        .existsByInstituteAndCourseCodeIgnoreCase(
                                course.getInstitute(),
                                courseCode
                        );

        if (exists &&
                !course.getCourseCode()
                        .equalsIgnoreCase(courseCode)) {

            throw new DuplicateResourceException(
                    "Course code already exists."
            );
        }
    }

    private void validateDuplicateCourseName(
            Course course,
            String courseName) {

        boolean exists =
                courseRepository
                        .existsByInstituteAndCourseNameIgnoreCase(
                                course.getInstitute(),
                                courseName
                        );

        if (exists &&
                !course.getCourseName()
                        .equalsIgnoreCase(courseName)) {

            throw new DuplicateResourceException(
                    "Course name already exists."
            );
        }
    }

//    private Course getCourseEntity(Long courseId) {
//
//        Institute institute = securityUtil.getCurrentInstitute();
//        return courseRepository
//                .findByIdAndActiveTrue(courseId)
//                .filter(course ->
//                        course.getInstitute() .getId()
//                                .equals(institute.getId()))
//                .orElseThrow(() ->
//                        new ResourceNotFoundException(
//                                "Course not found."
//                        ));
//    }

    private Course getCourseEntity(Long courseId) {

        return courseRepository
                .findByIdAndInstituteAndActiveTrue(
                        courseId,
                        securityUtil.getCurrentInstitute()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found."
                        ));
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long courseId) {

        Course course = getCourseEntity(courseId);

        return courseMapper.toResponse(course);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CourseResponse> getCourses(
            PaginationRequest request) {

        if (!CourseSortableFields.ALLOWED_FIELDS.contains(request.getSortBy())) {

            throw new InvalidRequestException(
                    "Invalid sort field : " + request.getSortBy()
            );
        }

        Pageable pageable =
                SpecificationBuilder.buildPageable(request);

        Institute institute =
                securityUtil.getCurrentInstitute();

        Specification<Course> specification =
                CourseSpecification.search(
                        institute,
                        request.getKeyword()
                );

        Page<Course> page =
                courseRepository.findAll(
                        specification,
                        pageable
                );

        List<CourseResponse> responses =
                page.getContent()
                        .stream()
                        .map(courseMapper::toResponse)
                        .toList();

        return PaginationUtils.buildPageResponse(
                page,
                responses
        );
    }

    @Override
    public void updateCourseStatus(
            Long courseId,
            CourseStatusRequest request) {

        Course course = getCourseEntity(courseId);

        course.setStatus(request.getStatus());

        courseRepository.save(course);
    }

    @Override
    public void deleteCourse(Long courseId) {

        Course course = getCourseEntity(courseId);

        course.setActive(false);

        courseRepository.save(course);
    }
}