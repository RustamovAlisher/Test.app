package uz.testplatform.dto.result;



import java.time.LocalDateTime;


public record ResultShort(
        Long id,
        String resultCode,
        String testTitle,
        String userFullName,
        Integer totalQuestions,
        Integer correctAnswers,
        Integer score,
        LocalDateTime finishedAt
) {}