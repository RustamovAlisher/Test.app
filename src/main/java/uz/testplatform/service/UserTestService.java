package uz.testplatform.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.testplatform.dto.result.ResultShort;
import uz.testplatform.dto.result.SubmitRequest;
import uz.testplatform.dto.test.TestStartResponse;
import uz.testplatform.dto.test.TestSummaryResponse;

public interface UserTestService {

    Page<TestSummaryResponse> getAvailableTests(Pageable pageable);

    TestStartResponse startTest(Long testId, String userEmail);

    ResultShort submitTest(SubmitRequest request, String userEmail);
    Page<ResultShort> getMyResults(String userEmail, Pageable pageable);
}