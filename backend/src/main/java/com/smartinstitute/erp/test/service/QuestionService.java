package com.smartinstitute.erp.test.service;

import com.smartinstitute.erp.test.dto.request.CreateQuestionRequest;
import com.smartinstitute.erp.test.dto.request.QuestionSearchRequest;
import com.smartinstitute.erp.test.dto.request.QuestionStatusRequest;
import com.smartinstitute.erp.test.dto.request.UpdateQuestionRequest;
import com.smartinstitute.erp.test.dto.response.QuestionResponse;

import java.util.List;

public interface QuestionService {

    QuestionResponse createQuestion(CreateQuestionRequest request);

    QuestionResponse updateQuestion(
            Long questionId,
            UpdateQuestionRequest request
    );

    QuestionResponse getQuestionById(Long questionId);

    List<QuestionResponse> getAllQuestions();

    List<QuestionResponse> getQuestionsByCourse(Long courseId);

    List<QuestionResponse> getQuestionsByTopic(Long topicId);

    void deleteQuestion(Long questionId);

    List<QuestionResponse> searchQuestions(QuestionSearchRequest request);

    void updateQuestionStatus(Long id, QuestionStatusRequest request);

    QuestionResponse duplicateQuestion(Long id);
}