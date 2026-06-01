package uz.testplatform.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.testplatform.entity.Result;
import uz.testplatform.exception.NotFoundException;
import uz.testplatform.exception.RequestException;
import uz.testplatform.repository.ResultRepository;
import uz.testplatform.service.PdfService;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PdfServiceImpl implements PdfService {

    private final ResultRepository resultRepository;

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");


    @Override
    public byte[] generateResultPdf(String resultCode, String userEmail) {

        log.info("PDF yaratish: resultCode={}, user={}", resultCode, userEmail);

        Result result = resultRepository.findByResultCode(resultCode)
                .orElseThrow(() -> {
                    log.warn("Natija topilmadi: resultCode={}", resultCode);
                    return new NotFoundException("Natija topilmadi");
                });

        if (!result.getUser().getEmail().equals(userEmail)) {
            log.warn("Boshqa userning natijasiga urinish: resultCode={}, user={}",
                    resultCode, userEmail);
            throw new RequestException("Bu natija sizga tegishli emas");
        }

        if (result.getFinishedAt() == null) {
            throw new RequestException("Test hali tugatilmagan");
        }

        try {
            return buildPdf(result);
        } catch (Exception e) {
            log.error("PDF yaratishda xatolik: {}", e.getMessage());
            throw new RuntimeException("PDF yaratib bo'lmadi");
        }
    }


    private byte[] buildPdf(Result result) throws Exception {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 50, 50);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // Shriftlar
        Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD,
                new Color(33, 37, 41));
        Font headerFont = new Font(Font.HELVETICA, 13, Font.BOLD,
                new Color(33, 37, 41));
        Font normalFont = new Font(Font.HELVETICA, 11, Font.NORMAL,
                new Color(73, 80, 87));
        Font labelFont = new Font(Font.HELVETICA, 10, Font.BOLD,
                new Color(108, 117, 125));
        Font scoreFont = new Font(Font.HELVETICA, 36, Font.BOLD,
                getScoreColor(result.getScore()));

        // ===== SARLAVHA =====
        Paragraph title = new Paragraph("TEST NATIJASI", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(5);
        document.add(title);

        Paragraph testName = new Paragraph(result.getTest().getTitle(), headerFont);
        testName.setAlignment(Element.ALIGN_CENTER);
        testName.setSpacingAfter(20);
        document.add(testName);

        document.add(new Chunk(new com.lowagie.text.pdf.draw.LineSeparator()));
        document.add(Chunk.NEWLINE);

        // ===== USER MA'LUMOTLARI =====
        String fullName = result.getUser().getFirstName()
                + " "
                + result.getUser().getLastName();

        PdfPTable userTable = new PdfPTable(2);
        userTable.setWidthPercentage(100);
        userTable.setSpacingAfter(15);

        addTableRow(userTable, "Ism Familiya:", fullName, labelFont, normalFont);
        addTableRow(userTable, "Email:", result.getUser().getEmail(), labelFont, normalFont);
        addTableRow(userTable, "Pasport kodi:", result.getUser().getPassportCode(), labelFont, normalFont);
        addTableRow(userTable, "Natija kodi:", result.getResultCode(), labelFont, normalFont);

        document.add(userTable);

        // ===== NATIJA SCORE =====
        Paragraph scoreLabel = new Paragraph("NATIJA", labelFont);
        scoreLabel.setAlignment(Element.ALIGN_CENTER);
        document.add(scoreLabel);

        Paragraph scorePara = new Paragraph(result.getScore() + "%", scoreFont);
        scorePara.setAlignment(Element.ALIGN_CENTER);
        scorePara.setSpacingAfter(10);
        document.add(scorePara);

        // ===== BATAFSIL NATIJA =====
        PdfPTable resultTable = new PdfPTable(2);
        resultTable.setWidthPercentage(60);
        resultTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        resultTable.setSpacingAfter(20);

        addTableRow(resultTable, "Jami savollar:",
                result.getTotalQuestions() + " ta", labelFont, normalFont);
        addTableRow(resultTable, "To'g'ri javoblar:",
                result.getCorrectAnswers() + " ta", labelFont, normalFont);
        addTableRow(resultTable, "Noto'g'ri javoblar:",
                (result.getTotalQuestions() - result.getCorrectAnswers()) + " ta",
                labelFont, normalFont);

        document.add(resultTable);

        // ===== VAQT MA'LUMOTLARI =====
        document.add(new Chunk(new com.lowagie.text.pdf.draw.LineSeparator()));
        document.add(Chunk.NEWLINE);

        PdfPTable timeTable = new PdfPTable(2);
        timeTable.setWidthPercentage(100);
        timeTable.setSpacingAfter(20);

        addTableRow(timeTable, "Boshlangan vaqt:",
                result.getStartedAt().format(DATE_FORMAT), labelFont, normalFont);
        addTableRow(timeTable, "Tugatilgan vaqt:",
                result.getFinishedAt().format(DATE_FORMAT), labelFont, normalFont);

        long durationMinutes = java.time.Duration.between(
                result.getStartedAt(), result.getFinishedAt()).toMinutes();
        addTableRow(timeTable, "Sarflangan vaqt:",
                durationMinutes + " daqiqa", labelFont, normalFont);

        document.add(timeTable);

        document.close();

        log.info("PDF yaratildi: resultCode={}", result.getResultCode());

        return outputStream.toByteArray();
    }


    private void addTableRow(PdfPTable table, String label, String value,
                             Font labelFont, Font valueFont) {

        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(4);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(4);
        table.addCell(valueCell);
    }


    private Color getScoreColor(int score) {
        if (score >= 80) {
            return new Color(40, 167, 69);
        }
        if (score >= 50) {
            return new Color(255, 193, 7);
        }
        return new Color(220, 53, 69);
    }
}