package uz.testplatform.dto.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import uz.testplatform.enums.TestLevel;

public record UpdateQuestionRequest(

        @NotBlank(message = "Savol matni bo'sh bo'lmasin")
        String text,

        @NotNull(message = "Savol darajasi bo'sh bo'lmasin")
        TestLevel level
) {
}