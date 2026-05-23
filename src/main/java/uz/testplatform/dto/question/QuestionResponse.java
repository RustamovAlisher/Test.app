package uz.testplatform.dto.question;

import uz.testplatform.dto.variant.VariantResponse;

import java.util.List;

public record QuestionResponse(
        Long id,
        String text,
        List<VariantResponse> variants
) {
}