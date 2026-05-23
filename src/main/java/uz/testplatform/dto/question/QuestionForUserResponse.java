package uz.testplatform.dto.question;

import uz.testplatform.dto.variant.VariantForUserResponse;

import java.util.List;

public record QuestionForUserResponse(
        Long id,
        String text,
        List<VariantForUserResponse> variants
) {
}