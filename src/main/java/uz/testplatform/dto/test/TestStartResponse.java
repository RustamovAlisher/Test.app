package uz.testplatform.dto.test;

import uz.testplatform.dto.question.QuestionForUserResponse;

import java.time.LocalDateTime;
import java.util.List;

public record TestStartResponse(
        Long resultId,
        String title,
        Integer durationMinutes,
        LocalDateTime startedAt,
        List<QuestionForUserResponse> questions
) {
}