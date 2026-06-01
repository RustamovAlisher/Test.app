package uz.testplatform.dto.test;

import java.time.LocalDateTime;

public record TestResponse(
        Long id,
        String title,
        Integer durationMinutes,
        Integer easyCount,
        Integer mediumCount,
        Integer hardCount,
        LocalDateTime createdAt,

        // Statistika — bankda nechta savol bor
        Long easyAvailable,
        Long mediumAvailable,
        Long hardAvailable,
        boolean isReady
) {
}