package uz.testplatform.dto.variant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record CreateVariantRequest(

        @NotBlank(message = "Variant matni bo'sh bo'lmasin")
        @Size(min = 1, max = 500, message = "Variant matni 1-500 ta belgi bo'lishi kerak")
        String text,

        @NotNull(message = "isCorrect maydoni majburiy (true yoki false)")
        Boolean isCorrect
) {
}