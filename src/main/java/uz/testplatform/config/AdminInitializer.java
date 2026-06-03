package uz.testplatform.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.testplatform.entity.User;
import uz.testplatform.enums.Role;
import uz.testplatform.repository.UserRepository;


@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.first-name}")
    private String adminFirstName;

    @Value("${app.admin.last-name}")
    private String adminLastName;

    @Value("${app.admin.passport-code}")
    private String adminPassportCode;


    @Override
    public void run(String... args) {

        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin mavjud: {}", adminEmail);
            return;
        }

        User admin = User.builder()
                .firstName(adminFirstName)
                .lastName(adminLastName)
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .passportCode(adminPassportCode)
                .role(Role.ADMIN)
                .build();

        userRepository.save(admin);


        log.info(" admin yaratildi:");
        log.info("  Email: {}", adminEmail);
        log.info("  Parol: {} (application.properties'da)", adminPassword);
        log.info("  Role:  ADMIN");

    }
}