package uz.testplatform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.testplatform.dto.result.ResultFull;
import uz.testplatform.dto.result.ResultShort;
import uz.testplatform.service.AdminResultService;

/**
 * Admin Result Controller — admin uchun natija boshqaruvi.
 *
 * Endpoint'lar:
 *   GET /admin/results          → barcha natijalar (pagination)
 *   GET /admin/results/{id}     → bitta natija batafsil
 */
@RestController
@RequestMapping("/admin/results")
@RequiredArgsConstructor
@Tag(name = "Admin Result", description = "Admin uchun natija boshqaruvi")
public class AdminResultController {

    private final AdminResultService adminResultService;


    @Operation(summary = "Barcha natijalar ro'yxati (pagination)")
    @GetMapping
    public ResponseEntity<Page<ResultShort>> getAllResults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ResultShort> results = adminResultService.getAllResults(pageable);
        return ResponseEntity.ok(results);
    }


    @Operation(summary = "Bitta natija batafsil (user va test bilan)")
    @GetMapping("/{id}")
    public ResponseEntity<ResultFull> getResultById(@PathVariable Long id) {
        ResultFull result = adminResultService.getResultById(id);
        return ResponseEntity.ok(result);
    }
}