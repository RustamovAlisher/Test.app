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
import uz.testplatform.dto.result.ResultShort;
import uz.testplatform.dto.result.SubmitRequest;
import uz.testplatform.dto.test.TestStartResponse;
import uz.testplatform.dto.test.TestSummaryResponse;
import uz.testplatform.service.UserTestService;

import java.security.Principal;

@RestController
@RequestMapping("/tests")
@RequiredArgsConstructor
@Tag(name = "User Test", description = "User uchun test endpointlari")
public class UserTestController {

    private final UserTestService userTestService;
    @Operation(summary = "Mavjud testlar ro'yxati")
    @GetMapping
    public ResponseEntity<Page<TestSummaryResponse>> getAvailableTests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userTestService.getAvailableTests(pageable));
    }

    @Operation(summary = "Testni boshlash - savollar generatsiya bo'ladi")
    @PostMapping("/{id}/start")
    public ResponseEntity<TestStartResponse> startTest(
            @PathVariable Long id,
            Principal principal) {

        String userEmail = principal.getName();
        return ResponseEntity.ok(userTestService.startTest(id, userEmail));
    }

    @Operation(summary = "Javoblarni jo'natish - score hisoblanadi")
    @PostMapping("/submit")
    public ResponseEntity<ResultShort> submitTest(
            @Valid @RequestBody SubmitRequest request,
            Principal principal) {

        String userEmail = principal.getName();
        return ResponseEntity.ok(userTestService.submitTest(request, userEmail));
    }

    @Operation(summary = "Mening natijalarim")
    @GetMapping("/my-results")
    public ResponseEntity<Page<ResultShort>> getMyResults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Principal principal) {

        String userEmail = principal.getName();
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userTestService.getMyResults(userEmail, pageable));
    }
}