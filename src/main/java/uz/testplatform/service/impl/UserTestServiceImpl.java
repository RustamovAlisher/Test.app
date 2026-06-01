package uz.testplatform.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.testplatform.dto.question.QuestionForUserResponse;
import uz.testplatform.dto.result.ResultShort;
import uz.testplatform.dto.result.SubmitRequest;
import uz.testplatform.dto.test.TestStartResponse;
import uz.testplatform.dto.test.TestSummaryResponse;
import uz.testplatform.entity.*;
import uz.testplatform.enums.TestLevel;
import uz.testplatform.exception.NotFoundException;
import uz.testplatform.exception.RequestException;
import uz.testplatform.mapper.QuestionMapper;
import uz.testplatform.mapper.ResultMapper;
import uz.testplatform.mapper.TestMapper;
import uz.testplatform.repository.QuestionRepository;
import uz.testplatform.repository.ResultRepository;
import uz.testplatform.repository.TestRepository;
import uz.testplatform.repository.UserRepository;
import uz.testplatform.service.UserTestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserTestServiceImpl implements UserTestService {

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final ResultRepository resultRepository;
    private final UserRepository userRepository;
    private final QuestionMapper questionMapper;
    private final TestMapper testMapper;
    private final ResultMapper resultMapper;


    // ============================================================
    // 1. Testlar ro'yxati (user uchun, qisqa, savolsiz)
    // ============================================================
    @Override
    public Page<TestSummaryResponse> getAvailableTests(Pageable pageable) {

        log.info("User uchun testlar so'raldi: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Test> testPage = testRepository.findAll(pageable);
        Page<TestSummaryResponse> responsePage = testPage.map(testMapper::toSummaryResponse);

        log.info("Topilgan testlar soni: {}", responsePage.getNumberOfElements());

        return responsePage;
    }


    // ============================================================
    // 2. Test boshlash - savollar generatsiya bo'ladi
    // ============================================================
    @Override
    public TestStartResponse startTest(Long testId, String userEmail) {

        log.info("Test boshlash: testId={}, user={}", testId, userEmail);

        // 1. Userni topish
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("Foydalanuvchi topilmadi"));

        // 2. Testni topish
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> {
                    log.warn("Test topilmadi: id={}", testId);
                    return new NotFoundException("Test topilmadi");
                });

        // 3. Bazadan har darajadagi savollarni hisoblash (test tayyormi?)
        long easyAvailable = questionRepository.countByTestIdAndLevel(testId, TestLevel.EASY);
        long mediumAvailable = questionRepository.countByTestIdAndLevel(testId, TestLevel.MEDIUM);
        long hardAvailable = questionRepository.countByTestIdAndLevel(testId, TestLevel.HARD);
        long totalAvailable = easyAvailable + mediumAvailable + hardAvailable;

        int requiredTotal = test.getEasyCount() + test.getMediumCount() + test.getHardCount();

        // 4. Umumiy savollar yetadimi?
        if (totalAvailable < requiredTotal) {
            log.warn("Test tayyor emas: testId={}, kerak={}, bor={}",
                    testId, requiredTotal, totalAvailable);
            throw new RequestException("Test hali tayyor emas, keyinroq urinib ko'ring");
        }

        // 5. Savollarni generatsiya qilish (fallback bilan)
        List<Question> selectedQuestions = generateQuestions(
                test,
                (int) easyAvailable,
                (int) mediumAvailable,
                (int) hardAvailable
        );

        // 6. Savollarni aralashtirish (har user uchun boshqa tartib)
        Collections.shuffle(selectedQuestions);

        // 7. Result entity yaratish (hozir test boshlandi)
        Result result = Result.builder()
                .user(user)
                .test(test)
                .totalQuestions(selectedQuestions.size())
                .correctAnswers(0)
                .score(0)
                .startedAt(LocalDateTime.now())
                .finishedAt(null)
                .build();

        // 8. Har savol uchun UserAnswer yaratish (boshida bo'sh, javob yo'q)
        for (Question question : selectedQuestions) {
            UserAnswer answer = UserAnswer.builder()
                    .result(result)
                    .question(question)
                    .selectedVariant(null)
                    .correct(false)
                    .build();

            result.getAnswers().add(answer);
        }

        // 9. Result va UserAnswer'larni saqlash (cascade)
        // 9. Result va UserAnswer'larni saqlash (cascade)
        Result savedResult = resultRepository.save(result);

        // >>> SHU YERGA YANGI QISM QO'SHILDI (N+1 optimizatsiyasi) >>>
        List<Long> selectedIds = new ArrayList<>();
        for (Question q : selectedQuestions) {
            selectedIds.add(q.getId());
        }
        List<Question> questionsWithVariants = questionRepository.findWithVariants(selectedIds);
        // <<< SHU YERGACHA <<<

        log.info("Test boshlandi: resultId={}, savollar soni={}",
                savedResult.getId(), questionsWithVariants.size()); // questionsWithVariants bo'ldi

        // 10. User uchun javob tayyorlash (variantlar ham aralashgan)
        List<QuestionForUserResponse> questionResponses = new ArrayList<>();
        for (Question question : questionsWithVariants) { // selectedQuestions o'rniga questionsWithVariants bo'ldi

            // Variantlarni aralashtirish (har user uchun)
            List<Variant> shuffledVariants = new ArrayList<>(question.getVariants());
            Collections.shuffle(shuffledVariants);

            // Vaqtinchalik question yaratamiz aralashgan variantlar bilan
            Question tempQuestion = new Question();
            tempQuestion.setId(question.getId());
            tempQuestion.setText(question.getText());
            tempQuestion.setVariants(shuffledVariants);

            questionResponses.add(questionMapper.toUserResponse(tempQuestion));
        }

        return new TestStartResponse(
                savedResult.getId(),
                test.getTitle(),
                test.getDurationMinutes(),
                savedResult.getStartedAt(),
                questionResponses
        );

    }
    // ============================================================
    // Helper - savollarni generatsiya qilish (fallback bilan)
    // ============================================================
    private List<Question> generateQuestions(
            Test test,
            int easyAvailable,
            int mediumAvailable,
            int hardAvailable
    ) {
        log.info("Savollar generatsiya: easy={}, medium={}, hard={} (talab: {}/{}/{}, bor: {}/{}/{})",
                test.getEasyCount(), test.getMediumCount(), test.getHardCount(),
                test.getEasyCount(), test.getMediumCount(), test.getHardCount(),
                easyAvailable, mediumAvailable, hardAvailable);

        int needEasy = test.getEasyCount();
        int needMedium = test.getMediumCount();
        int needHard = test.getHardCount();

        // ============ HARD ============
        int hardToTake;
        int hardShortage = 0;

        if (hardAvailable >= needHard) {
            hardToTake = needHard;
        } else {
            hardToTake = easyAvailable >= 0 ? (int) Math.min(hardAvailable, needHard) : 0;
            hardToTake = hardAvailable;
            hardShortage = needHard - hardAvailable;
            log.info("HARD yetishmadi: kerak={}, bor={}, kamomad={} (MEDIUM ga o'tadi)",
                    needHard, hardAvailable, hardShortage);
        }

        // ============ MEDIUM ============
        // Medium talab = asl talab + HARD dan kelgan kamomad
        int mediumNeedTotal = needMedium + hardShortage;
        int mediumToTake;
        int mediumShortage = 0;

        if (mediumAvailable >= mediumNeedTotal) {
            mediumToTake = mediumNeedTotal;
        } else {
            mediumToTake = mediumAvailable;
            mediumShortage = mediumNeedTotal - mediumAvailable;
            log.info("MEDIUM yetishmadi: kerak={}, bor={}, kamomad={} (EASY ga o'tadi)",
                    mediumNeedTotal, mediumAvailable, mediumShortage);
        }

        // ============ EASY ============
        // Easy talab = asl talab + MEDIUM dan kelgan kamomad
        int easyNeedTotal = needEasy + mediumShortage;
        int easyToTake;
        int easyShortage = 0;

        if (easyAvailable >= easyNeedTotal) {
            easyToTake = easyNeedTotal;
        } else {
            easyToTake = easyAvailable;
            easyShortage = easyNeedTotal - easyAvailable;
            log.info("EASY yetishmadi: kerak={}, bor={}, kamomad={} (yuqoriga - MEDIUM/HARD)",
                    easyNeedTotal, easyAvailable, easyShortage);
        }

        // ============ EASY YETISHMASA - YUQORIGA KO'TARILISH ============
        // EASY kam bo'lsa va medium/hard'da zaxira bo'lsa - qo'shamiz
        if (easyShortage > 0) {
            int mediumExtra = mediumAvailable - mediumToTake;
            int hardExtra = hardAvailable - hardToTake;

            if (mediumExtra > 0) {
                int addFromMedium;
                if (mediumExtra >= easyShortage) {
                    addFromMedium = easyShortage;
                } else {
                    addFromMedium = mediumExtra;
                }
                mediumToTake = mediumToTake + addFromMedium;
                easyShortage = easyShortage - addFromMedium;
                log.info("EASY kamomad MEDIUM dan to'ldirildi: {} ta", addFromMedium);
            }

            if (easyShortage > 0 && hardExtra > 0) {
                int addFromHard;
                if (hardExtra >= easyShortage) {
                    addFromHard = easyShortage;
                } else {
                    addFromHard = hardExtra;
                }
                hardToTake = hardToTake + addFromHard;
                easyShortage = easyShortage - addFromHard;
                log.info("EASY kamomad HARD dan to'ldirildi: {} ta", addFromHard);
            }
        }

        // ============ Yakuniy tekshirish ============
        int totalSelected = easyToTake + mediumToTake + hardToTake;
        int totalRequired = needEasy + needMedium + needHard;

        if (totalSelected < totalRequired) {
            log.warn("Yetarli savol topilmadi: kerak={}, topildi={}", totalRequired, totalSelected);
            throw new RequestException("Test hali tayyor emas, keyinroq urinib ko'ring");
        }

        // ============ Bazadan RANDOM tanlash ============
        List<Question> result = new ArrayList<>();

        if (easyToTake > 0) {
            List<Question> easyQuestions = questionRepository.findRandomByTestIdAndLevel(
                    test.getId(), TestLevel.EASY.name(), easyToTake);
            result.addAll(easyQuestions);
        }

        if (mediumToTake > 0) {
            List<Question> mediumQuestions = questionRepository.findRandomByTestIdAndLevel(
                    test.getId(), TestLevel.MEDIUM.name(), mediumToTake);
            result.addAll(mediumQuestions);
        }

        if (hardToTake > 0) {
            List<Question> hardQuestions = questionRepository.findRandomByTestIdAndLevel(
                    test.getId(), TestLevel.HARD.name(), hardToTake);
            result.addAll(hardQuestions);
        }

        log.info("Generatsiya tugadi: easy={}, medium={}, hard={}, jami={}",
                easyToTake, mediumToTake, hardToTake, result.size());

        return result;
    }


    // ============================================================
    // 3. Test javoblarini jo'natish - score hisoblanadi
    // ============================================================
    @Override
    public ResultShort submitTest(SubmitRequest request, String userEmail) {

        log.info("Test submit: resultId={}, user={}", request.resultId(), userEmail);

        // 1. Userni topish
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("Foydalanuvchi topilmadi"));

        // 2. Result'ni javoblari bilan birga olish (JOIN FETCH)
        Result result = resultRepository.findByIdWithAnswers(request.resultId())
                .orElseThrow(() -> {
                    log.warn("Result topilmadi: id={}", request.resultId());
                    return new NotFoundException("Test natijasi topilmadi");
                });

        // 3. Bu user'ning result'imi tekshirish (xavfsizlik)
        if (!result.getUser().getId().equals(user.getId())) {
            log.warn("Boshqa userning result'iga urinish: resultId={}, user={}",
                    request.resultId(), userEmail);
            throw new RequestException("Bu test sizga tegishli emas");
        }

        // 4. Test allaqachon tugatilgan bo'lsa
        if (result.getFinishedAt() != null) {
            log.warn("Test allaqachon tugatilgan: resultId={}", request.resultId());
            throw new RequestException("Bu test allaqachon tugatilgan");
        }

        // 5. Vaqt tekshirish (informatsion log uchun, javoblar baribir qabul qilinadi)
        LocalDateTime deadline = result.getStartedAt().plusMinutes(result.getTest().getDurationMinutes());
        boolean timeExpired = LocalDateTime.now().isAfter(deadline);

        if (timeExpired) {
            log.info("Vaqt tugagan: resultId={}, javoblar baribir qabul qilinadi", request.resultId());
        }

        // 6. Har userAnswer ni yangilash
        int correctCount = 0;

        // Result'dagi har UserAnswer uchun
        for (UserAnswer userAnswer : result.getAnswers()) {

            // Bu savol uchun user javob bermi?
            SubmitRequest.UserAnswer foundAnswer = null;

            for (SubmitRequest.UserAnswer submitted : request.answers()) {
                if (submitted.questionId().equals(userAnswer.getQuestion().getId())) {
                    foundAnswer = submitted;
                    break;
                }
            }

            // Agar javob bermagan bo'lsa - noto'g'ri (boshida shunday qolavergan)
            if (foundAnswer == null) {
                userAnswer.setSelectedVariant(null);
                userAnswer.setCorrect(false);
                continue;
            }

            // Tanlangan variantni topish
            Variant selectedVariant = null;
            for (Variant variant : userAnswer.getQuestion().getVariants()) {
                if (variant.getId().equals(foundAnswer.selectedVariantId())) {
                    selectedVariant = variant;
                    break;
                }
            }

            // Variant topilmasa - cheating (bu savolga noto'g'ri variant ID yuborilgan)
            if (selectedVariant == null) {
                log.warn("Variant topilmadi: questionId={}, variantId={}",
                        foundAnswer.questionId(), foundAnswer.selectedVariantId());
                userAnswer.setSelectedVariant(null);
                userAnswer.setCorrect(false);
                continue;
            }

            // Javobni saqlash
            userAnswer.setSelectedVariant(selectedVariant);
            userAnswer.setCorrect(selectedVariant.getIsCorrect());

            if (selectedVariant.getIsCorrect()) {
                correctCount = correctCount + 1;
            }
        }

        // 7. Score hisoblash (foiz)
        int score = (correctCount * 100) / result.getTotalQuestions();

        // 8. Result yangilash
        result.setCorrectAnswers(correctCount);
        result.setScore(score);
        result.setFinishedAt(LocalDateTime.now());

        Result savedResult = resultRepository.save(result);

        log.info("Test yakunlandi: resultId={}, correctAnswers={}/{}, score={}%",
                savedResult.getId(), correctCount, result.getTotalQuestions(), score);

        return resultMapper.toShort(savedResult);
    }


    // ============================================================
    // 4. User o'z natijalarini ko'rish
    // ============================================================
    @Override
    public Page<ResultShort> getMyResults(String userEmail, Pageable pageable) {

        log.info("User natijalarini olish: user={}", userEmail);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("Foydalanuvchi topilmadi"));

        Page<Result> resultPage = resultRepository.findByUserIdWithTest(user.getId(), pageable);        Page<ResultShort> responsePage = resultPage.map(resultMapper::toShort);

        log.info("Topilgan natijalar soni: {}", responsePage.getNumberOfElements());

        return responsePage;
    }
}