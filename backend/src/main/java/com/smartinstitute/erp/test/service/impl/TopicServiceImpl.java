package com.smartinstitute.erp.test.service.impl;

import com.smartinstitute.erp.common.service.BaseCrudService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.course.entity.Course;
import com.smartinstitute.erp.course.repository.CourseRepository;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.security.util.SecurityUtil;
import com.smartinstitute.erp.test.dto.request.CreateTopicRequest;
import com.smartinstitute.erp.test.dto.request.UpdateTopicRequest;
import com.smartinstitute.erp.test.dto.response.TopicResponse;
import com.smartinstitute.erp.test.entity.Topic;
import com.smartinstitute.erp.test.mapper.TopicMapper;
import com.smartinstitute.erp.test.repository.QuestionRepository;
import com.smartinstitute.erp.test.repository.TopicRepository;
import com.smartinstitute.erp.test.service.TopicService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TopicServiceImpl extends BaseCrudService implements TopicService {

    private final TopicRepository topicRepository;

    private final CourseRepository courseRepository;

    private final TopicMapper topicMapper;

    private final QuestionRepository questionRepository;

    public TopicServiceImpl(SecurityUtil securityUtil, InstituteAccessValidator instituteAccessValidator, TopicRepository topicRepository, CourseRepository courseRepository, TopicMapper topicMapper, QuestionRepository questionRepository) {
        super(securityUtil, instituteAccessValidator);
        this.topicRepository = topicRepository;
        this.courseRepository = courseRepository;
        this.topicMapper = topicMapper;
        this.questionRepository = questionRepository;
    }

    /**
     * Returns currently logged-in institute.
     */
//    private Institute getCurrentInstitute() {
//        return getCurrentInstitute();
//    }

    /**
     * Returns active topic or throws exception.
     */
    private Topic getTopicOrThrow(Long topicId) {
        return topicRepository.findByIdAndActiveTrue(topicId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Topic not found."));
    }

    /**
     * Returns course belonging to current institute.
     */
    private Course getCourseOrThrow(Long courseId) {

        Institute institute = getCurrentInstitute();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Course not found."));

        if (!course.getInstitute().getId().equals(institute.getId())) {
            throw new IllegalArgumentException(
                    "Course does not belong to current institute.");
        }

        return course;
    }

    @Override
    public TopicResponse createTopic(
            CreateTopicRequest request
    ) {

        Institute institute = getCurrentInstitute();

        Course course = getCourseOrThrow(request.getCourseId());
        String topicName = request.getName().trim();
        boolean exists = topicRepository.existsByInstituteAndCourseAndNameAndActiveTrue(
                institute,
                course,
                topicName
        );

        if (exists) {
            throw new IllegalArgumentException(
                    "Topic with the same name already exists for this course."
            );
        }

        Topic topic = new Topic();

        topic.setName(topicName);

        topic.setDescription(request.getDescription());

        topic.setDisplayOrder(request.getDisplayOrder());

        topic.setCourse(course);

        topic.setInstitute(institute);

        topic = topicRepository.save(topic);

        return topicMapper.toResponse(topic);
    }

    @Override
    public TopicResponse updateTopic(
            Long topicId,
            UpdateTopicRequest request
    ) {

        Topic topic = getTopicOrThrow(topicId);

        Institute institute = getCurrentInstitute();

        Course course = getCourseOrThrow(request.getCourseId());

        String topicName = request.getName().trim();

        boolean exists = topicRepository
                .existsByInstituteAndCourseAndNameAndIdNotAndActiveTrue(
                        institute,
                        course,
                        topicName,
                        topicId
                );

        if (exists) {
            throw new IllegalArgumentException(
                    "Topic with the same name already exists for this course."
            );
        }

        topic.setName(topicName);

        topic.setDescription(request.getDescription());

        topic.setDisplayOrder(request.getDisplayOrder());

        topic.setActive(request.getActive());

        topic.setCourse(course);

        topic = topicRepository.save(topic);

        return topicMapper.toResponse(topic);
    }

    @Override
    public void deleteTopic(Long topicId) {

        Topic topic = getTopicOrThrow(topicId);

        topic.setActive(false);

        topicRepository.save(topic);
    }

    @Override
    @Transactional(readOnly = true)
    public TopicResponse getTopicById(Long topicId) {

        Topic topic = getTopicOrThrow(topicId);

        return topicMapper.toResponse(topic);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopicResponse> getAllTopics() {

        Institute institute = getCurrentInstitute();

        return topicRepository
                .findByInstituteAndActiveTrueOrderByDisplayOrderAscNameAsc(institute)
                .stream()
                .map(topicMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopicResponse> getTopicsByCourse(Long courseId) {

        Course course = getCourseOrThrow(courseId);

        return topicRepository
                .findByCourseAndActiveTrueOrderByDisplayOrderAscNameAsc(course)
                .stream()
                .map(topicMapper::toResponse)
                .toList();
    }

}