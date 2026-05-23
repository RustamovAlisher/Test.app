package uz.testplatform.dto.result;

import uz.testplatform.enums.TestLevel;

import java.time.LocalDateTime;


public record ResultShort(
        Long id,
        String testTitle,
        TestLevel testLevel,
        String userFullName,
        Integer totalQuestions,
        Integer correctAnswers,
        Integer score,
        LocalDateTime finishedAt
) {
}