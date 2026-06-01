package uz.testplatform.dto.test;

import jakarta.validation.constraints.*;

public record CreateTestRequest(

        @NotBlank(message = "Test nomi bo'sh bo'lmasin")
        String title,

        @NotNull(message = "Davomiylik bo'sh bo'lmasin")
        @Min(value = 1, message = "Davomiylik kamida 1 daqiqa bo'lishi kerak")
        @Max(value = 300, message = "Davomiylik 300 daqiqadan oshmasin")
        Integer durationMinutes,

        @NotNull(message = "Easy savollar soni bo'sh bo'lmasin")
        @Min(value = 0, message = "Easy soni manfiy bo'lmasin")
        Integer easyCount,

        @NotNull(message = "Medium savollar soni bo'sh bo'lmasin")
        @Min(value = 0, message = "Medium soni manfiy bo'lmasin")
        Integer mediumCount,

        @NotNull(message = "Hard savollar soni bo'sh bo'lmasin")
        @Min(value = 0, message = "Hard soni manfiy bo'lmasin")
        Integer hardCount
) {
}