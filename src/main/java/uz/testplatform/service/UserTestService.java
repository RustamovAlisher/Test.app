package uz.testplatform.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.testplatform.dto.result.ResultShort;
import uz.testplatform.dto.result.SubmitRequest;
import uz.testplatform.dto.test.TestStartResponse;
import uz.testplatform.dto.test.TestSummaryResponse;

public interface UserTestService {

    // User uchun testlar ro'yxati (pagination)
    Page<TestSummaryResponse> getAvailableTests(Pageable pageable);

    // Test boshlash - savollar generatsiya bo'ladi (10 easy + 5 medium + 5 hard)
    TestStartResponse startTest(Long testId, String userEmail);

    // Javoblarni jo'natish - score hisoblanadi
    ResultShort submitTest(SubmitRequest request, String userEmail);

    // User o'zining natijalarini ko'rish
    Page<ResultShort> getMyResults(String userEmail, Pageable pageable);
}