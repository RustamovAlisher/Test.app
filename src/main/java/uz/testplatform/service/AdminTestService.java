package uz.testplatform.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.testplatform.dto.test.CreateTestRequest;
import uz.testplatform.dto.test.TestResponse;
import uz.testplatform.dto.test.TestSummaryResponse;
import uz.testplatform.dto.test.UpdateTestRequest;

public interface AdminTestService {

    TestResponse createTest(CreateTestRequest request);

    TestResponse updateTest(Long testId, UpdateTestRequest request);

    void deleteTest(Long testId);

    Page<TestSummaryResponse> getAllTests(Pageable pageable);

    TestResponse getTestById(Long testId);
}