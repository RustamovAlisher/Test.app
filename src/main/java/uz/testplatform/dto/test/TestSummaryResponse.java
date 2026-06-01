package uz.testplatform.dto.test;

public record TestSummaryResponse(
        Long id,
        String title,
        Integer durationMinutes,
        Integer easyCount,
        Integer mediumCount,
        Integer hardCount
) {
}