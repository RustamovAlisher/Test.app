package uz.testplatform.service;

import uz.testplatform.dto.test.CreateTestRequest;
import uz.testplatform.dto.test.TestResponse;
import uz.testplatform.dto.test.UpdateTestRequest;

import java.util.List;


public interface AdminTestService {


    TestResponse createTest(CreateTestRequest request);

    TestResponse updateTest(Long testId, UpdateTestRequest request);

    void deleteTest(Long testId);

    List<TestResponse> getAllTests();

    TestResponse getTestById(Long testId);

}