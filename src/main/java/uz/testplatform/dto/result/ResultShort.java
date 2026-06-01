package uz.testplatform.dto.result;



import java.time.LocalDateTime;


public record ResultShort(
        Long id,
        String resultCode,        // ← avval qo'shgan edik
        String testTitle,
        String userFullName,
        Integer totalQuestions,
        Integer correctAnswers,
        Integer score,
        LocalDateTime finishedAt
) {}