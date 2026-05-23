package uz.testplatform.dto.test;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import uz.testplatform.dto.question.CreateQuestionRequest;
import uz.testplatform.enums.TestLevel;

import java.util.List;

public record CreateTestRequest(

        @NotBlank(message = "Test nomi bo'sh bo'lmasin")
        String title,

        String description,

        @NotNull(message = "Test darajasi bo'sh bo'lmasin")
        TestLevel level,

        @NotNull(message = "Davomiylik bo'sh bo'lmasin")
        @Min(value = 1, message = "Davomiylik kamida 1 daqiqa bo'lishi kerak")
        @Max(value = 300, message = "Davomiylik 300 daqiqadan oshmasin")
        Integer durationMinutes,

        @NotNull(message = "Savollar soni bo'sh bo'lmasin")
        @Min(value = 1, message = "Kamida 1 ta savol bo'lishi kerak")
        @Max(value = 100, message = "100 tadan ko'p savol bo'lmasin")
        Integer questionCount,

        @NotEmpty(message = "Kamida 1 ta savol bo'lishi kerak")
        @Valid
        List<CreateQuestionRequest> questions
) {
}