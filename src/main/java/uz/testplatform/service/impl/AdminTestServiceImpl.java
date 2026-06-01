package uz.testplatform.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.testplatform.dto.test.CreateTestRequest;
import uz.testplatform.dto.test.TestResponse;
import uz.testplatform.dto.test.TestSummaryResponse;
import uz.testplatform.dto.test.UpdateTestRequest;
import uz.testplatform.entity.Test;
import uz.testplatform.enums.TestLevel;
import uz.testplatform.exception.NotFoundException;
import uz.testplatform.mapper.TestMapper;
import uz.testplatform.repository.QuestionRepository;
import uz.testplatform.repository.TestRepository;
import uz.testplatform.service.AdminTestService;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AdminTestServiceImpl implements AdminTestService {

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final TestMapper testMapper;


    // Yangi test yaratish (savolsiz)
    @Override
    public TestResponse createTest(CreateTestRequest request) {

        log.info("Yangi test yaratish: {}", request.title());

        Test test = testMapper.toEntity(request);
        Test savedTest = testRepository.save(test);

        log.info("Test yaratildi: id={}, title={}", savedTest.getId(), savedTest.getTitle());

        // Yangi test - hali savol yo'q, statistika 0
        return buildTestResponse(savedTest);
    }


    // Testni tahrirlash
    @Override
    public TestResponse updateTest(Long testId, UpdateTestRequest request) {

        log.info("Test tahrirlash: id={}", testId);

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> {
                    log.warn("Test topilmadi: id={}", testId);
                    return new NotFoundException("Test topilmadi");
                });

        test.setTitle(request.title());
        test.setDurationMinutes(request.durationMinutes());
        test.setEasyCount(request.easyCount());
        test.setMediumCount(request.mediumCount());
        test.setHardCount(request.hardCount());

        Test updatedTest = testRepository.save(test);

        log.info("Test yangilandi: id={}", updatedTest.getId());

        return buildTestResponse(updatedTest);
    }


    // Testni o'chirish
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


    // Barcha testlar ro'yxati (pagination, savolsiz)
    @Override
    public Page<TestSummaryResponse> getAllTests(Pageable pageable) {

        log.info("Testlar so'raldi: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Test> testPage = testRepository.findAll(pageable);
        Page<TestSummaryResponse> responsePage = testPage.map(testMapper::toSummaryResponse);

        log.info("Topilgan testlar soni: {} (jami: {})",
                responsePage.getNumberOfElements(), responsePage.getTotalElements());

        return responsePage;
    }


    // Bitta testni ko'rish (statistika bilan)
    @Override
    public TestResponse getTestById(Long testId) {

        log.info("Test so'raldi: id={}", testId);

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> {
                    log.warn("Test topilmadi: id={}", testId);
                    return new NotFoundException("Test topilmadi");
                });

        return buildTestResponse(test);
    }


    // Helper - test javobini statistika bilan tayyorlash
    private TestResponse buildTestResponse(Test test) {

        // Har darajadagi savollar sonini hisoblash (3 ta COUNT so'rovi)
        long easyAvailable = questionRepository.countByTestIdAndLevel(test.getId(), TestLevel.EASY);
        long mediumAvailable = questionRepository.countByTestIdAndLevel(test.getId(), TestLevel.MEDIUM);
        long hardAvailable = questionRepository.countByTestIdAndLevel(test.getId(), TestLevel.HARD);

        return testMapper.toResponse(test, easyAvailable, mediumAvailable, hardAvailable);
    }
}