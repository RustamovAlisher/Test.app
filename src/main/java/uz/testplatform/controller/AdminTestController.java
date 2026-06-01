package uz.testplatform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.testplatform.dto.test.CreateTestRequest;
import uz.testplatform.dto.test.TestResponse;
import uz.testplatform.dto.test.TestSummaryResponse;
import uz.testplatform.dto.test.UpdateTestRequest;
import uz.testplatform.service.AdminTestService;

/**
 * Admin Test Controller — test boshqaruvi (savolsiz).
 */
@RestController
@RequestMapping("/admin/tests")
@RequiredArgsConstructor
@Tag(name = "Admin Test", description = "Admin uchun test boshqaruvi")
public class AdminTestController {

    private final AdminTestService adminTestService;


    @Operation(summary = "Yangi test yaratish (savolsiz)")
    @PostMapping
    public ResponseEntity<TestResponse> createTest(@Valid @RequestBody CreateTestRequest request) {
        TestResponse response = adminTestService.createTest(request);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Testni tahrirlash")
    @PutMapping("/{id}")
    public ResponseEntity<TestResponse> updateTest(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTestRequest request) {

        TestResponse response = adminTestService.updateTest(id, request);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Testni o'chirish")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTest(@PathVariable Long id) {
        adminTestService.deleteTest(id);
        return ResponseEntity.ok("Test muvaffaqiyatli o'chirildi");
    }


    @Operation(summary = "Barcha testlar ro'yxati (sahifalash bilan)")
    @GetMapping
    public ResponseEntity<Page<TestSummaryResponse>> getAllTests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<TestSummaryResponse> tests = adminTestService.getAllTests(pageable);
        return ResponseEntity.ok(tests);
    }


    @Operation(summary = "Bitta testni ko'rish (statistika bilan)")
    @GetMapping("/{id}")
    public ResponseEntity<TestResponse> getTestById(@PathVariable Long id) {
        TestResponse response = adminTestService.getTestById(id);
        return ResponseEntity.ok(response);
    }
}