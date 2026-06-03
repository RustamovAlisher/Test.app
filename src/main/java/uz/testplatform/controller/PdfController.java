package uz.testplatform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.testplatform.repository.ResultRepository;
import uz.testplatform.service.PdfService;
import uz.testplatform.mapper.ResultMapper;

import java.security.Principal;


@RestController
@RequestMapping("/results")
@RequiredArgsConstructor
@Tag(name = "Result PDF", description = "Natija PDF va QR code")
public class PdfController {

    private final PdfService pdfService;
    private final ResultRepository resultRepository;
    private final ResultMapper resultMapper;

    @Operation(summary = "Natijani PDF qilib yuklab olish")
    @GetMapping("/{resultCode}/pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @PathVariable String resultCode,
            Principal principal) {
        String userEmail = principal.getName();
        byte[] pdfBytes = pdfService.generateResultPdf(resultCode, userEmail);
        String fileName = "natija-" + resultCode.substring(0, 8) + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .body(pdfBytes);
    }




}