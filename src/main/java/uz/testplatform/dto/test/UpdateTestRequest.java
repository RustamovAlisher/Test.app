package uz.testplatform.dto.test;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import uz.testplatform.enums.TestLevel;

public record UpdateTestRequest(

        @NotBlank(message = "Test nomi bo'sh bo'lmasin")
        String title,

        String description,

        @NotNull(message = "Test darajasi bo'sh bo'lmasin")
        TestLevel level,

        @NotNull(message = "Davomiylik bo'sh bo'lmasin")
        @Min(1)
        @Max(300)
        Integer durationMinutes,

        @NotNull(message = "Savollar soni bo'sh bo'lmasin")
        @Min(1)
        @Max(100)
        Integer questionCount
) {
}