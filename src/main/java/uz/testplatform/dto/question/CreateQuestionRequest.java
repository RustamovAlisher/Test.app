package uz.testplatform.dto.question;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import uz.testplatform.dto.variant.CreateVariantRequest;
import uz.testplatform.enums.TestLevel;

import java.util.List;

public record CreateQuestionRequest(

        @NotBlank(message = "Savol matni bo'sh bo'lmasin")
        String text,

        @NotNull(message = "Savol darajasi bo'sh bo'lmasin")
        TestLevel level,

        @NotEmpty(message = "Kamida 2 ta variant bo'lishi kerak")
        @Size(min = 2, message = "Kamida 2 ta variant bo'lishi kerak")
        @Valid
        List<CreateVariantRequest> variants
) {
}