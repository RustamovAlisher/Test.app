package uz.testplatform.dto.variant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateVariantRequest(

        @NotBlank(message = "Variant matni bo'sh bo'lmasin")
        String text,

        @NotNull(message = "isCorrect maydoni majburiy")
        Boolean isCorrect
) {
}