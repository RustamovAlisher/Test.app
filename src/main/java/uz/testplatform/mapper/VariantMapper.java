package uz.testplatform.mapper;

import org.springframework.stereotype.Component;
import uz.testplatform.dto.variant.CreateVariantRequest;
import uz.testplatform.dto.variant.VariantForUserResponse;
import uz.testplatform.dto.variant.VariantResponse;
import uz.testplatform.entity.Variant;


@Component
public class VariantMapper {
    public Variant toEntity(CreateVariantRequest request) {
        return Variant.builder()
                .text(request.text())
                .isCorrect(request.isCorrect())
                .build();
    }

    public VariantResponse toResponse(Variant variant) {
        return new VariantResponse(
                variant.getId(),
                variant.getText(),
                variant.getIsCorrect()
        );
    }

    public VariantForUserResponse toUserResponse(Variant variant) {
        return new VariantForUserResponse(
                variant.getId(),
                variant.getText()
        );
    }
}