package uz.testplatform.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uz.testplatform.dto.question.CreateQuestionRequest;
import uz.testplatform.dto.question.QuestionForUserResponse;
import uz.testplatform.dto.question.QuestionResponse;
import uz.testplatform.dto.test.CreateTestRequest;
import uz.testplatform.dto.test.TestResponse;
import uz.testplatform.dto.test.TestStartResponse;
import uz.testplatform.dto.test.TestSummaryResponse;
import uz.testplatform.entity.Question;
import uz.testplatform.entity.Test;

import java.time.LocalDateTime;
import java.util.List;


@Component
@RequiredArgsConstructor
public class TestMapper {

    private final QuestionMapper questionMapper;


    public Test toEntity(CreateTestRequest request) {
        Test test = Test.builder()
                .title(request.title())
                .description(request.description())
                .level(request.level())
                .durationMinutes(request.durationMinutes())
                .questionCount(request.questionCount())
                .build();

        for (CreateQuestionRequest questionRequest : request.questions()) {
            Question question = questionMapper.toEntity(questionRequest);
            question.setTest(test);
            test.getQuestions().add(question);
        }

        return test;
    }


    public TestResponse toResponse(Test test) {
        List<QuestionResponse> questionResponses = test.getQuestions().stream()
                .map(questionMapper::toResponse)
                .toList();

        return new TestResponse(
                test.getId(),
                test.getTitle(),
                test.getDescription(),
                test.getLevel(),
                test.getDurationMinutes(),
                test.getQuestionCount(),
                test.getCreatedAt(),
                questionResponses
        );
    }


    public TestSummaryResponse toSummaryResponse(Test test) {
        return new TestSummaryResponse(
                test.getId(),
                test.getTitle(),
                test.getDescription(),
                test.getLevel(),
                test.getDurationMinutes(),
                test.getQuestionCount()
        );
    }


    public TestStartResponse toStartResponse(
            Test test,
            List<Question> questions,
            Long resultId,
            LocalDateTime startedAt
    ) {
        List<QuestionForUserResponse> questionResponses = questions.stream()
                .map(questionMapper::toUserResponse)
                .toList();

        return new TestStartResponse(
                resultId,
                test.getTitle(),
                test.getLevel(),
                test.getDurationMinutes(),
                startedAt,
                questionResponses
        );
    }
}