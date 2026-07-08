package com.smartinstitute.erp.test.service.impl;

import com.smartinstitute.erp.common.exception.ResourceNotFoundException;
import com.smartinstitute.erp.common.service.BaseCrudService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.course.entity.Course;
import com.smartinstitute.erp.course.repository.CourseRepository;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.security.util.SecurityUtil;
import com.smartinstitute.erp.test.dto.request.*;
import com.smartinstitute.erp.test.dto.response.QuestionResponse;
import com.smartinstitute.erp.test.entity.Question;
import com.smartinstitute.erp.test.entity.QuestionOption;
import com.smartinstitute.erp.test.entity.Topic;
import com.smartinstitute.erp.test.mapper.QuestionMapper;
import com.smartinstitute.erp.test.repository.QuestionRepository;
import com.smartinstitute.erp.test.repository.TopicRepository;
import com.smartinstitute.erp.test.service.QuestionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class QuestionServiceImpl extends BaseCrudService implements QuestionService {

    private final QuestionRepository questionRepository;

    private final TopicRepository topicRepository;

    private final CourseRepository courseRepository;

    private final QuestionMapper questionMapper;

    public QuestionServiceImpl(SecurityUtil securityUtil, InstituteAccessValidator instituteAccessValidator, TopicRepository topicRepository, CourseRepository courseRepository, QuestionRepository questionRepository, QuestionMapper questionMapper) {
        super(securityUtil, instituteAccessValidator);
        this.topicRepository = topicRepository;
        this.courseRepository = courseRepository;
        this.questionRepository = questionRepository;
        this.questionMapper = questionMapper;
    }

    //    ---------------
    @Override
    public QuestionResponse createQuestion(CreateQuestionRequest request) {

        Institute institute = getCurrentInstitute();

        validateDuplicateQuestion(request.getQuestionText());

        Course course = getCourse(request.getCourseId());

        Topic topic = getTopic(request.getTopicId());

        validateQuestionOptions(request.getOptions());

        Question question = new Question();

        question.setInstitute(institute);
        question.setCourse(course);
        question.setTopic(topic);

        question.setQuestionText(request.getQuestionText());
        question.setQuestionType(request.getQuestionType());
        question.setDifficulty(request.getDifficulty());
        question.setExplanation(request.getExplanation());
        question.setMarks(request.getMarks());

        addQuestionOptions(question, request.getOptions());

        Question savedQuestion = questionRepository.save(question);

        return questionMapper.toResponse(savedQuestion);
    }

    @Override
    public QuestionResponse updateQuestion(Long questionId,
                                           UpdateQuestionRequest request) {

        Question question = getQuestion(questionId);

        validateDuplicateQuestion(
                questionId,
                request.getQuestionText()
        );

        Course course = getCourse(request.getCourseId());

        Topic topic = getTopic(request.getTopicId());

        validateQuestionOptions(request.getOptions());

        question.setCourse(course);
        question.setTopic(topic);

        question.setQuestionText(request.getQuestionText());
        question.setQuestionType(request.getQuestionType());
        question.setDifficulty(request.getDifficulty());
        question.setExplanation(request.getExplanation());
        question.setMarks(request.getMarks());

        updateQuestionOptions(question, request.getOptions());

        Question updatedQuestion = questionRepository.save(question);

        return questionMapper.toResponse(updatedQuestion);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionResponse getQuestionById(Long questionId) {

        Question question = getQuestion(questionId);

        return questionMapper.toResponse(question);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponse> getAllQuestions() {

        Institute institute = getCurrentInstitute();

        return questionRepository
                .findByInstituteAndActiveTrue(institute)
                .stream()
                .map(questionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuestionsByCourse(Long courseId) {

        Institute institute = getCurrentInstitute();

        Course course = getCourse(courseId);

        return questionRepository
                .findByCourseAndInstituteAndActiveTrue(
                        course,
                        institute
                )
                .stream()
                .map(questionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuestionsByTopic(Long topicId) {

        Institute institute = getCurrentInstitute();

        Topic topic = getTopic(topicId);

        return questionRepository
                .findByTopicAndInstituteAndActiveTrue(
                        topic,
                        institute
                )
                .stream()
                .map(questionMapper::toResponse)
                .toList();
    }

    @Override
    public void deleteQuestion(Long questionId) {

        Question question = getQuestion(questionId);
        question.setActive(false);

        questionRepository.save(question);

    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponse> searchQuestions(QuestionSearchRequest request) {

        Course course = null;
        Topic topic = null;

        if (request.getCourseId() != null) {
            course = getCourse(request.getCourseId());
        }

        if (request.getTopicId() != null) {
            topic = getTopic(request.getTopicId());
        }

        return questionRepository.searchQuestions(
                        getCurrentInstitute(),
                        course,
                        topic,
                        request.getDifficulty(),
                        request.getQuestionType(),
                        request.getKeyword()
                )
                .stream()
                .map(questionMapper::toResponse)
                .toList();

    }

    @Override
    public void updateQuestionStatus(Long id, QuestionStatusRequest request) {

        Question question = getQuestion(id);

        question.setActive(request.getActive());

        questionRepository.save(question);

    }

    @Override
    public QuestionResponse duplicateQuestion(Long id) {

        Question existingQuestion = getQuestion(id);

        Question duplicate = new Question();

        duplicate.setQuestionText(
                existingQuestion.getQuestionText() + " (Copy)"
        );

        duplicate.setQuestionType(existingQuestion.getQuestionType());

        duplicate.setDifficulty(existingQuestion.getDifficulty());

        duplicate.setExplanation(existingQuestion.getExplanation());

        duplicate.setMarks(existingQuestion.getMarks());

        duplicate.setCourse(existingQuestion.getCourse());

        duplicate.setTopic(existingQuestion.getTopic());

        duplicate.setInstitute(existingQuestion.getInstitute());

        duplicate.setDisplayOrder(
                questionRepository.findMaxDisplayOrderByTopic(existingQuestion.getTopic()) + 1
        );

        duplicate.setActive(true);

        for (QuestionOption option : existingQuestion.getOptions()) {

            QuestionOption copiedOption = new QuestionOption();

            copiedOption.setOptionText(option.getOptionText());

            copiedOption.setCorrect(option.getCorrect());

            copiedOption.setDisplayOrder(option.getDisplayOrder());

            copiedOption.setActive(true);

            copiedOption.setQuestion(duplicate);

            duplicate.getOptions().add(copiedOption);

        }

        Question savedQuestion = questionRepository.save(duplicate);

        return questionMapper.toResponse(savedQuestion);

    }

    private Course getCourse(Long courseId) {

        return courseRepository
                .findByIdAndInstituteAndActiveTrue(courseId, getCurrentInstitute())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found with id : " + courseId
                        )
                );
    }

    private Topic getTopic(Long topicId) {

        Institute institute = getCurrentInstitute();

        return topicRepository
                .findByIdAndInstituteAndActiveTrue(
                        topicId,
                        institute
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Topic not found with id : " + topicId
                        )
                );
    }

    private Question getQuestion(Long questionId) {

        Institute institute = getCurrentInstitute();

        return questionRepository
                .findByIdAndInstitute(
                        questionId,
                        institute
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Question not found with id : " + questionId
                        )
                );
    }

    private void validateDuplicateQuestion(String questionText) {

        Institute institute = getCurrentInstitute();

        boolean exists =
                questionRepository.existsByInstituteAndQuestionTextAndActiveTrue(
                        institute,
                        questionText
                );

        if (exists) {
            throw new IllegalArgumentException(
                    "Question already exists."
            );
        }

    }

    private void validateDuplicateQuestion(Long questionId, String questionText) {

        Institute institute = getCurrentInstitute();

        boolean exists =
                questionRepository
                        .existsByInstituteAndQuestionTextAndIdNotAndActiveTrue(
                                institute,
                                questionText,
                                questionId
                        );

        if (exists) {
            throw new IllegalArgumentException("Question already exists.");
        }

    }

    private void validateQuestionOptions(
            List<QuestionOptionRequest> options
    ) {

        long correctAnswers = options.stream()
                .filter(QuestionOptionRequest::getCorrect)
                .count();

        if (correctAnswers == 0) {
            throw new IllegalArgumentException(
                    "At least one option must be correct."
            );
        }

    }

    private void addQuestionOptions(Question question, List<QuestionOptionRequest> options) {

        for (QuestionOptionRequest optionRequest : options) {

            QuestionOption option = new QuestionOption();

            option.setQuestion(question);
            option.setOptionText(optionRequest.getOptionText());
            option.setCorrect(optionRequest.getCorrect());
            option.setDisplayOrder(optionRequest.getDisplayOrder());
            option.setActive(true);

            question.getOptions().add(option);

        }

    }

    private void updateQuestionOptions(Question question,
                                       List<QuestionOptionRequest> optionRequests) {

        question.getOptions().clear();
        addQuestionOptions(question, optionRequests);

    }

}