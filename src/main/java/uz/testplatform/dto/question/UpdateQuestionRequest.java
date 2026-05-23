package uz.testplatform.dto.question;

import jakarta.validation.constraints.NotBlank;

public record UpdateQuestionRequest(

        @NotBlank(message = "Savol matni bo'sh bo'lmasin")
        String text
) {
}