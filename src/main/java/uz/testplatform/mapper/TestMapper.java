package uz.testplatform.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uz.testplatform.dto.test.CreateTestRequest;
import uz.testplatform.dto.test.TestResponse;
import uz.testplatform.dto.test.TestSummaryResponse;
import uz.testplatform.entity.Test;

@Component
@RequiredArgsConstructor
public class TestMapper {

    // DTO -> Entity (test yaratishda - savolsiz)
    public Test toEntity(CreateTestRequest request) {
        return Test.builder()
                .title(request.title())
                .durationMinutes(request.durationMinutes())
                .easyCount(request.easyCount())
                .mediumCount(request.mediumCount())
                .hardCount(request.hardCount())
                .build();
    }


    // Entity -> Response (statistika bilan)
    public TestResponse toResponse(
            Test test,
            long easyAvailable,
            long mediumAvailable,
            long hardAvailable
    ) {
        // Test tayyormi - har darajada yetarli savol bormi
        boolean isReady = easyAvailable >= test.getEasyCount()
                && mediumAvailable >= test.getMediumCount()
                && hardAvailable >= test.getHardCount();

        return new TestResponse(
                test.getId(),
                test.getTitle(),
                test.getDurationMinutes(),
                test.getEasyCount(),
                test.getMediumCount(),
                test.getHardCount(),
                test.getCreatedAt(),
                easyAvailable,
                mediumAvailable,
                hardAvailable,
                isReady
        );
    }


    // Entity -> Summary (ro'yxat uchun)
    public TestSummaryResponse toSummaryResponse(Test test) {
        return new TestSummaryResponse(
                test.getId(),
                test.getTitle(),
                test.getDurationMinutes(),
                test.getEasyCount(),
                test.getMediumCount(),
                test.getHardCount()
        );
    }
}