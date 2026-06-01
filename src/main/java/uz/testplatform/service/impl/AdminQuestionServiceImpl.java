package uz.testplatform.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.testplatform.dto.question.CreateQuestionRequest;
import uz.testplatform.dto.question.QuestionResponse;
import uz.testplatform.dto.question.UpdateQuestionRequest;
import uz.testplatform.entity.Question;
import uz.testplatform.entity.Test;
import uz.testplatform.enums.TestLevel;
import uz.testplatform.exception.NotFoundException;
import uz.testplatform.mapper.QuestionMapper;
import uz.testplatform.repository.QuestionRepository;
import uz.testplatform.repository.TestRepository;
import uz.testplatform.service.AdminQuestionService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AdminQuestionServiceImpl implements AdminQuestionService {

    private final QuestionRepository questionRepository;
    private final TestRepository testRepository;
    private final QuestionMapper questionMapper;

    @Override
    public QuestionResponse addQuestion(Long testId, CreateQuestionRequest request) {

        log.info("Testga savol qo'shish: testId={}, level={}", testId, request.level());
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> {
                    log.warn("Test topilmadi: id={}", testId);
                    return new NotFoundException("Test topilmadi");
                });

        Question question = questionMapper.toEntity(request);
        question.setTest(test);
        Question savedQuestion = questionRepository.save(question);
        log.info("Savol qo'shildi: id={}, testId={}", savedQuestion.getId(), testId);
        return questionMapper.toResponse(savedQuestion);
    }

    @Override
    public QuestionResponse updateQuestion(Long questionId, UpdateQuestionRequest request) {

        log.info("Savol tahrirlash: id={}", questionId);

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> {
                    log.warn("Savol topilmadi: id={}", questionId);
                    return new NotFoundException("Savol topilmadi");
                });

        question.setText(request.text());
        question.setLevel(request.level());

        Question updatedQuestion = questionRepository.save(question);

        log.info("Savol yangilandi: id={}", updatedQuestion.getId());

        return questionMapper.toResponse(updatedQuestion);
    }

    @Override
    public void deleteQuestion(Long questionId) {

        log.info("Savol o'chirish: id={}", questionId);

        if (!questionRepository.existsById(questionId)) {
            log.warn("Savol topilmadi: id={}", questionId);
            throw new NotFoundException("Savol topilmadi");
        }

        questionRepository.deleteById(questionId);

        log.info("Savol o'chirildi: id={}", questionId);
    }

    @Override
    public List<QuestionResponse> getQuestionsByTest(Long testId) {

        log.info("Test savollari so'raldi: testId={}", testId);
        if (!testRepository.existsById(testId)) {
            log.warn("Test topilmadi: id={}", testId);
            throw new NotFoundException("Test topilmadi");
        }
        List<Question> allQuestions = new ArrayList<>();
        allQuestions.addAll(questionRepository.findByTestIdAndLevelWithVariants(testId, TestLevel.EASY));
        allQuestions.addAll(questionRepository.findByTestIdAndLevelWithVariants(testId, TestLevel.MEDIUM));
        allQuestions.addAll(questionRepository.findByTestIdAndLevelWithVariants(testId, TestLevel.HARD));

        List<QuestionResponse> responses = new ArrayList<>();
        for (Question question : allQuestions) {
            responses.add(questionMapper.toResponse(question));
        }

        log.info("Topilgan savollar soni: {}", responses.size());

        return responses;
    }
}