package uz.testplatform.dto.test;

import uz.testplatform.dto.question.QuestionForUserResponse;
import uz.testplatform.enums.TestLevel;

import java.time.LocalDateTime;
import java.util.List;

public record TestStartResponse(
        Long resultId,
        String title,
        TestLevel level,
        Integer durationMinutes,
        LocalDateTime startedAt,
        List<QuestionForUserResponse> questions
) {
}