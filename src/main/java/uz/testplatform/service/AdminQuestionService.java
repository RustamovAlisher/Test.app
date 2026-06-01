package uz.testplatform.service;

import uz.testplatform.dto.question.CreateQuestionRequest;
import uz.testplatform.dto.question.QuestionResponse;
import uz.testplatform.dto.question.UpdateQuestionRequest;

import java.util.List;

public interface AdminQuestionService {

    // Testga yangi savol qo'shish
    QuestionResponse addQuestion(Long testId, CreateQuestionRequest request);

    // Savolni tahrirlash (faqat text va level)
    QuestionResponse updateQuestion(Long questionId, UpdateQuestionRequest request);

    // Savolni o'chirish
    void deleteQuestion(Long questionId);

    // Test bo'yicha barcha savollar (admin ko'radi, daraja bilan)
    List<QuestionResponse> getQuestionsByTest(Long testId);
}