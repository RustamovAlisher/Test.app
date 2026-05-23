package uz.testplatform.dto.statistics;

public record AdminStats(
        Long totalUsers,
        Long totalTests,
        Long totalResults,
        Double averageScore
) {
}