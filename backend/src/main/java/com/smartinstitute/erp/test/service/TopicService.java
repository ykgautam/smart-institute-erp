package com.smartinstitute.erp.test.service;

import com.smartinstitute.erp.test.dto.request.CreateTopicRequest;
import com.smartinstitute.erp.test.dto.request.UpdateTopicRequest;
import com.smartinstitute.erp.test.dto.response.TopicResponse;

import java.util.List;

public interface TopicService {

    /**
     * Create a new topic.
     */
    TopicResponse createTopic(CreateTopicRequest request);

    /**
     * Update an existing topic.
     */
    TopicResponse updateTopic(Long topicId, UpdateTopicRequest request);

    /**
     * Soft delete a topic.
     */
    void deleteTopic(Long topicId);

    /**
     * Get topic by id.
     */
    TopicResponse getTopicById(Long topicId);

    /**
     * Get all active topics of current institute.
     */
    List<TopicResponse> getAllTopics();

    /**
     * Get all active topics of a course.
     */
    List<TopicResponse> getTopicsByCourse(Long courseId);

}