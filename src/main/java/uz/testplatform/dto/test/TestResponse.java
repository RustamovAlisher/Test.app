package uz.testplatform.dto.test;

import uz.testplatform.dto.question.QuestionResponse;
import uz.testplatform.enums.TestLevel;

import java.time.LocalDateTime;
import java.util.List;

public record TestResponse(
        Long id,
        String title,
        String description,
        TestLevel level,
        Integer durationMinutes,
        Integer questionCount,
        LocalDateTime createdAt,
        List<QuestionResponse> questions
) {
}