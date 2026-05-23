package uz.testplatform.dto.test;

import uz.testplatform.enums.TestLevel;

public record TestSummaryResponse(
        Long id,
        String title,
        String description,
        TestLevel level,
        Integer durationMinutes,
        Integer questionCount
) {
}