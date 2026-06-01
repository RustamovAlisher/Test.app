package uz.testplatform.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uz.testplatform.dto.question.CreateQuestionRequest;
import uz.testplatform.dto.question.QuestionForUserResponse;
import uz.testplatform.dto.question.QuestionResponse;
import uz.testplatform.dto.variant.CreateVariantRequest;
import uz.testplatform.dto.variant.VariantForUserResponse;
import uz.testplatform.dto.variant.VariantResponse;
import uz.testplatform.entity.Question;
import uz.testplatform.entity.Variant;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class QuestionMapper {

    private final VariantMapper variantMapper;

    public Question toEntity(CreateQuestionRequest request) {
        Question question = Question.builder()
                .text(request.text())
                .level(request.level())
                .build();

        for (CreateVariantRequest variantRequest : request.variants()) {
            Variant variant = variantMapper.toEntity(variantRequest);
            variant.setQuestion(question);
            question.getVariants().add(variant);
        }

        return question;
    }

    public QuestionResponse toResponse(Question question) {
        List<VariantResponse> variantResponses = new ArrayList<>();
        for (Variant variant : question.getVariants()) {
            variantResponses.add(variantMapper.toResponse(variant));
        }

        return new QuestionResponse(
                question.getId(),
                question.getText(),
                question.getLevel(),
                variantResponses
        );
    }

    public QuestionForUserResponse toUserResponse(Question question) {
        List<VariantForUserResponse> variantResponses = new ArrayList<>();
        for (Variant variant : question.getVariants()) {
            variantResponses.add(variantMapper.toUserResponse(variant));
        }

        return new QuestionForUserResponse(
                question.getId(),
                question.getText(),
                variantResponses
        );
    }
}