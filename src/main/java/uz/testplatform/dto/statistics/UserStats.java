package uz.testplatform.dto.statistics;

public record UserStats(
        Long userId,
        String userFullName,
        Long totalTestsTaken,
        Double averageScore,
        Integer bestScore,
        Integer worstScore
) {
}