package uz.testplatform.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import uz.testplatform.exception.RequestException;
import uz.testplatform.service.EmailService;

/**
 * Email Service implementation.
 *
 * Gmail SMTP orqali oddiy matnli email yuboradi.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    // application.properties dan email manzili
    @Value("${spring.mail.username}")
    private String fromEmail;

    // application.properties dan base URL (localhost yoki domain)
    @Value("${app.base-url}")
    private String baseUrl;


    @Override
    public void sendEmailChangeConfirmation (String email, String token, String userName) {

        log.info("Email yuborish boshlandi: {}", email);

        try {
            // 1. Email obyektini yaratish
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            // 2. Email sozlamalari
            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("Email tasdiqlash - Test Platform");

            // 3. Tasdiqlash linkini yaratish
            String link = baseUrl + "/auth/confirm-email?token=" + token;

            // 4. Email matnini yaratish
            String body = "Salom " + userName + "!\n\n"
                    + "Email manzilingizni o'zgartirish uchun quyidagi linkni bosing:\n"
                    + link + "\n\n"
                    + "Link 30 daqiqa amal qiladi.\n\n"
                    + "Agar siz so'ramagan bo'lsangiz, bu xatni e'tiborsiz qoldiring.";

            helper.setText(body, false);

            // 5. Emailni yuborish
            mailSender.send(mimeMessage);

            log.info("Email muvaffaqiyatli yuborildi: {}", email);

        } catch (MessagingException e) {
            log.error("Email yuborishda xatolik: {}", e.getMessage());
            throw new RequestException("Email yuborishda xatolik yuz berdi");
        }
    }
}