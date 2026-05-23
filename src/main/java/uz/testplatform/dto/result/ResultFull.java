package uz.testplatform.dto.result;

import uz.testplatform.enums.TestLevel;

import java.time.LocalDateTime;


public record ResultFull(
        Long id,
        String resultCode,
        String userFullName,
        String userEmail,
        String passportCode,
        String testTitle,
        TestLevel testLevel,
        Integer totalQuestions,
        Integer correctAnswers,
        Integer score,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        Long durationSeconds
) {
}