package uz.testplatform.dto.question;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import uz.testplatform.dto.variant.CreateVariantRequest;

import java.util.List;

public record CreateQuestionRequest(

        @NotBlank(message = "Savol matni bo'sh bo'lmasin")
        String text,

        @NotEmpty(message = "Kamida 2 ta variant bo'lishi kerak")
        @Valid
        List<CreateVariantRequest> variants
) {
}