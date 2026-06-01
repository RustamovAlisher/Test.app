package uz.testplatform.dto.question;

import uz.testplatform.dto.variant.VariantResponse;
import uz.testplatform.enums.TestLevel;

import java.util.List;

public record QuestionResponse(
        Long id,
        String text,
        TestLevel level,
        List<VariantResponse> variants
) {
}