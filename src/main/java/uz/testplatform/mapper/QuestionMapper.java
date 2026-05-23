package uz.testplatform.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uz.testplatform.dto.question.CreateQuestionRequest;
import uz.testplatform.dto.question.QuestionForUserResponse;
import uz.testplatform.dto.question.QuestionResponse;
import uz.testplatform.dto.variant.CreateVariantRequest;
import uz.testplatform.entity.Question;
import uz.testplatform.entity.Variant;

import java.util.List;


@Component
@RequiredArgsConstructor
public class QuestionMapper {

    private final VariantMapper variantMapper;


    public Question toEntity(CreateQuestionRequest request) {
        Question question = Question.builder()
                .text(request.text())
                .build();

        for (CreateVariantRequest variantRequest : request.variants()) {
            Variant variant = variantMapper.toEntity(variantRequest);
            variant.setQuestion(question);
            question.getVariants().add(variant);
        }

        return question;
    }


    public QuestionResponse toResponse(Question question) {
        List<uz.testplatform.dto.variant.VariantResponse> variantResponses =
                question.getVariants().stream()
                        .map(variantMapper::toResponse)
                        .toList();

        return new QuestionResponse(
                question.getId(),
                question.getText(),
                variantResponses
        );
    }


    public QuestionForUserResponse toUserResponse(Question question) {
        List<uz.testplatform.dto.variant.VariantForUserResponse> variantResponses =
                question.getVariants().stream()
                        .map(variantMapper::toUserResponse)
                        .toList();

        return new QuestionForUserResponse(
                question.getId(),
                question.getText(),
                variantResponses
        );
    }
}