package uz.testplatform.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.testplatform.dto.result.ResultFull;
import uz.testplatform.dto.result.ResultShort;

public interface AdminResultService {
    Page<ResultShort> getAllResults(Pageable pageable);
    ResultFull getResultById(Long resultId);
}