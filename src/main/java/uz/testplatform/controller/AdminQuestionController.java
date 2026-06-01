package uz.testplatform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.testplatform.dto.question.CreateQuestionRequest;
import uz.testplatform.dto.question.QuestionResponse;
import uz.testplatform.dto.question.UpdateQuestionRequest;
import uz.testplatform.service.AdminQuestionService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Tag(name = "Admin Question", description = "Admin uchun savol boshqaruvi")
public class AdminQuestionController {

    private final AdminQuestionService adminQuestionService;


    @Operation(summary = "Testga savol qo'shish")
    @PostMapping("/admin/tests/{testId}/questions")
    public ResponseEntity<QuestionResponse> addQuestion(
            @PathVariable Long testId,
            @Valid @RequestBody CreateQuestionRequest request) {

        QuestionResponse response = adminQuestionService.addQuestion(testId, request);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Test savollari ro'yxati (daraja bilan)")
    @GetMapping("/admin/tests/{testId}/questions")
    public ResponseEntity<List<QuestionResponse>> getQuestionsByTest(@PathVariable Long testId) {
        List<QuestionResponse> questions = adminQuestionService.getQuestionsByTest(testId);
        return ResponseEntity.ok(questions);
    }


    @Operation(summary = "Savolni tahrirlash")
    @PutMapping("/admin/questions/{id}")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody UpdateQuestionRequest request) {

        QuestionResponse response = adminQuestionService.updateQuestion(id, request);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Savolni o'chirish")
    @DeleteMapping("/admin/questions/{id}")
    public ResponseEntity<String> deleteQuestion(@PathVariable Long id) {
        adminQuestionService.deleteQuestion(id);
        return ResponseEntity.ok("Savol muvaffaqiyatli o'chirildi");
    }
}