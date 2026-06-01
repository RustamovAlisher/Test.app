package uz.testplatform.dto.result;

import java.time.LocalDateTime;


public record ResultFull(
        Long id,
        String resultCode,
        String userFullName,
        String userEmail,
        String passportCode,
        String testTitle,
        Integer totalQuestions,
        Integer correctAnswers,
        Integer score,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        Long durationSeconds
) {
}