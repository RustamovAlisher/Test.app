package uz.testplatform.dto.variant;

public record VariantResponse(
        Long id,
        String text,
        Boolean isCorrect
) {
}