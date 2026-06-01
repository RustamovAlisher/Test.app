package uz.testplatform.dto.test;

import jakarta.validation.constraints.*;

public record UpdateTestRequest(

        @NotBlank(message = "Test nomi bo'sh bo'lmasin")
        String title,

        @NotNull(message = "Davomiylik bo'sh bo'lmasin")
        @Min(value = 1, message = "Davomiylik kamida 1 daqiqa bo'lishi kerak")
        @Max(value = 300, message = "Davomiylik 300 daqiqadan oshmasin")
        Integer durationMinutes,

        @NotNull(message = "Easy savollar soni bo'sh bo'lmasin")
        @Min(value = 0)
        Integer easyCount,

        @NotNull(message = "Medium savollar soni bo'sh bo'lmasin")
        @Min(value = 0)
        Integer mediumCount,

        @NotNull(message = "Hard savollar soni bo'sh bo'lmasin")
        @Min(value = 0)
        Integer hardCount
) {
}