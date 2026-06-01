package uz.testplatform.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.testplatform.dto.result.ResultFull;
import uz.testplatform.dto.result.ResultShort;
import uz.testplatform.entity.Result;
import uz.testplatform.exception.NotFoundException;
import uz.testplatform.mapper.ResultMapper;
import uz.testplatform.repository.ResultRepository;
import uz.testplatform.service.AdminResultService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminResultServiceImpl implements AdminResultService {

    private final ResultRepository resultRepository;
    private final ResultMapper resultMapper;

    @Override
    public Page<ResultShort> getAllResults(Pageable pageable) {

        log.info("Admin: barcha natijalar so'raldi: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Result> resultPage = resultRepository.findAllWithUserAndTest(pageable);
        Page<ResultShort> responsePage = resultPage.map(resultMapper::toShort);

        log.info("Topilgan natijalar soni: {} (jami: {})",
                responsePage.getNumberOfElements(), responsePage.getTotalElements());

        return responsePage;
    }

    @Override
    public ResultFull getResultById(Long resultId) {

        log.info("Admin: natija batafsil so'raldi: id={}", resultId);

        Result result = resultRepository.findByIdWithUserAndTest(resultId)
                .orElseThrow(() -> {
                    log.warn("Natija topilmadi: id={}", resultId);
                    return new NotFoundException("Natija topilmadi");
                });

        return resultMapper.toFull(result);
    }
}