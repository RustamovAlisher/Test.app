package uz.testplatform.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.testplatform.dto.test.CreateTestRequest;
import uz.testplatform.dto.test.TestResponse;
import uz.testplatform.dto.test.UpdateTestRequest;
import uz.testplatform.entity.Test;
import uz.testplatform.exception.NotFoundException;
import uz.testplatform.mapper.TestMapper;
import uz.testplatform.repository.TestRepository;
import uz.testplatform.service.AdminTestService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AdminTestServiceImpl implements AdminTestService {

    private final TestRepository testRepository;
    private final TestMapper testMapper;


    @Override
    public TestResponse createTest(CreateTestRequest request) {

        log.info("Yangi test yaratish: {}", request.title());

        Test test = testMapper.toEntity(request);
        Test savedTest = testRepository.save(test);

        log.info("Test yaratildi: id={}, title={}", savedTest.getId(), savedTest.getTitle());

        return testMapper.toResponse(savedTest);
    }


    @Override
    public TestResponse updateTest(Long testId, UpdateTestRequest request) {

        log.info("Test tahrirlash: id={}", testId);

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> {
                    log.warn("Test topilmadi: id={}", testId);
                    return new NotFoundException("Test topilmadi");
                });

        test.setTitle(request.title());
        test.setDescription(request.description());
        test.setLevel(request.level());
        test.setDurationMinutes(request.durationMinutes());
        test.setQuestionCount(request.questionCount());

        Test updatedTest = testRepository.save(test);

        log.info("Test yangilandi: id={}", updatedTest.getId());

        return testMapper.toResponse(updatedTest);
    }


    @Override
    public void deleteTest(Long testId) {

        log.info("Test o'chirish: id={}", testId);

        if (!testRepository.existsById(testId)) {
            log.warn("Test topilmadi: id={}", testId);
            throw new NotFoundException("Test topilmadi");
        }

        testRepository.deleteById(testId);

        log.info("Test o'chirildi: id={}", testId);
    }


    @Override
    public List<TestResponse> getAllTests() {

        log.info("Barcha testlar so'raldi");

        List<Test> tests = testRepository.findAll();

        List<TestResponse> responses = new ArrayList<>();
        for (Test test : tests) {
            responses.add(testMapper.toResponse(test));
        }

        log.info("Topilgan testlar soni: {}", responses.size());

        return responses;
    }


    @Override
    public TestResponse getTestById(Long testId) {

        log.info("Test so'raldi: id={}", testId);

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> {
                    log.warn("Test topilmadi: id={}", testId);
                    return new NotFoundException("Test topilmadi");
                });

        return testMapper.toResponse(test);
    }
}