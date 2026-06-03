package uz.testplatform.service;

import uz.testplatform.dto.question.CreateQuestionRequest;
import uz.testplatform.dto.question.QuestionResponse;
import uz.testplatform.dto.question.UpdateQuestionRequest;

import java.util.List;

public interface AdminQuestionService {
    QuestionResponse addQuestion(Long testId, CreateQuestionRequest request);

    QuestionResponse updateQuestion(Long questionId, UpdateQuestionRequest request);

    void deleteQuestion(Long questionId);

    List<QuestionResponse> getQuestionsByTest(Long testId);

    List<QuestionResponse> addQuestions(Long testId, List<CreateQuestionRequest> requests);}