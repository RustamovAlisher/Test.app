package uz.testplatform.dto.result;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;


public record SubmitRequest(

        @NotNull(message = "Result ID bo'sh bo'lmasin")
        Long resultId,

        @NotEmpty(message = "Kamida 1 ta javob bo'lishi kerak")
        @Valid
        List<UserAnswer> answers
) {


    public record UserAnswer(
            @NotNull(message = "questionId bo'sh bo'lmasin")
            Long questionId,

            @NotNull(message = "selectedVariantId bo'sh bo'lmasin")
            Long selectedVariantId
    ) {}
}