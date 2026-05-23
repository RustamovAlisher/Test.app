package uz.testplatform.mapper;

import org.springframework.stereotype.Component;
import uz.testplatform.dto.result.ResultFull;
import uz.testplatform.dto.result.ResultShort;
import uz.testplatform.entity.Result;

import java.time.Duration;


@Component
public class ResultMapper {

    public ResultShort toShort(Result result) {
        String userFullName = result.getUser().getFirstName()
                + " "
                + result.getUser().getLastName();

        return new ResultShort(
                result.getId(),
                result.getTest().getTitle(),
                result.getTest().getLevel(),
                userFullName,
                result.getTotalQuestions(),
                result.getCorrectAnswers(),
                result.getScore(),
                result.getFinishedAt()
        );
    }


    public ResultFull toFull(Result result) {
        String userFullName = result.getUser().getFirstName()
                + " "
                + result.getUser().getLastName();

        long durationSeconds = Duration.between(
                result.getStartedAt(),
                result.getFinishedAt()
        ).toSeconds();

        return new ResultFull(
                result.getId(),
                result.getResultCode(),
                userFullName,
                result.getUser().getEmail(),
                result.getUser().getPassportCode(),
                result.getTest().getTitle(),
                result.getTest().getLevel(),
                result.getTotalQuestions(),
                result.getCorrectAnswers(),
                result.getScore(),
                result.getStartedAt(),
                result.getFinishedAt(),
                durationSeconds
        );
    }
}