package uz.testplatform.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.testplatform.dto.result.ResultFull;
import uz.testplatform.dto.result.ResultShort;

public interface AdminResultService {

    // Barcha natijalar (qisqa, pagination)
    Page<ResultShort> getAllResults(Pageable pageable);

    // Bitta natija batafsil (to'liq)
    ResultFull getResultById(Long resultId);
}