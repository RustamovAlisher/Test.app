package uz.testplatform.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.testplatform.dto.auth.ChangeEmailRequest;
import uz.testplatform.dto.auth.LoginRequest;
import uz.testplatform.dto.auth.LoginResponse;
import uz.testplatform.dto.auth.RegisterRequest;
import uz.testplatform.dto.user.UserResponse;
import uz.testplatform.entity.User;
import uz.testplatform.exception.AuthorizationException;
import uz.testplatform.exception.ConflictException;
import uz.testplatform.exception.NotFoundException;
import uz.testplatform.exception.RequestException;

import uz.testplatform.mapper.UserMapper;
import uz.testplatform.repository.UserRepository;
import uz.testplatform.service.AuthService;
import uz.testplatform.service.EmailService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EmailService emailService;


    @Override
    public UserResponse register(RegisterRequest request) {

        log.info("Ro'yxatdan o'tish boshlandi: {}", request.email());

        // Email band bo'lsa - xato
        if (userRepository.existsByEmail(request.email())) {
            log.warn("Email allaqachon band: {}", request.email());
            throw new ConflictException("Bu email allaqachon ro'yxatdan o'tgan");
        }

        // Pasport kodi band bo'lsa - xato
        if (userRepository.existsByPassportCode(request.passportCode())) {
            log.warn("Pasport kodi allaqachon band: {}", request.passportCode());
            throw new ConflictException("Bu pasport kodi allaqachon ro'yxatdan o'tgan");
        }

        // DTO ni Entity ga aylantirish va saqlash
        User user = userMapper.toEntity(request);
        User savedUser = userRepository.save(user);

        log.info("Yangi user yaratildi: id={}, email={}", savedUser.getId(), savedUser.getEmail());

        return userMapper.toResponse(savedUser);
    }


    @Override
    public LoginResponse login(LoginRequest request) {

        log.info("Login urinish: {}", request.email());

        // Email bo'yicha userni topish
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Login xato - email topilmadi: {}", request.email());
                    return new AuthorizationException("Email yoki parol noto'g'ri");
                });

        // Parolni tekshirish
        if (!user.getPassword().equals(request.password())) {
            log.warn("Login xato - parol noto'g'ri: {}", request.email());
            throw new AuthorizationException("Email yoki parol noto'g'ri");
        }

        log.info("Login muvaffaqiyatli: {}", user.getEmail());

        return new LoginResponse("", user.getEmail(), user.getRole());
    }


    @Override
    public void changeEmail(ChangeEmailRequest request, String currentUserEmail) {

        log.info("Email o'zgartirish so'rovi: {} -> {}", currentUserEmail, request.newEmail());

        // Hozirgi userni topish
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new NotFoundException("Foydalanuvchi topilmadi"));

        // Ism tekshirish
        if (!user.getFirstName().equals(request.firstName())) {
            log.warn("Email o'zgartirish - ism mos kelmadi: {}", currentUserEmail);
            throw new RequestException("Ma'lumotlar mos kelmadi");
        }

        // Familiya tekshirish
        if (!user.getLastName().equals(request.lastName())) {
            log.warn("Email o'zgartirish - familiya mos kelmadi: {}", currentUserEmail);
            throw new RequestException("Ma'lumotlar mos kelmadi");
        }

        // Pasport kodi tekshirish
        if (!user.getPassportCode().equals(request.passportCode())) {
            log.warn("Email o'zgartirish - pasport mos kelmadi: {}", currentUserEmail);
            throw new RequestException("Ma'lumotlar mos kelmadi");
        }

        // Yangi email band bo'lmasligi kerak
        if (userRepository.existsByEmail(request.newEmail())) {
            log.warn("Email o'zgartirish - yangi email band: {}", request.newEmail());
            throw new ConflictException("Bu email allaqachon band");
        }

        // UUID token yaratish
        String token = UUID.randomUUID().toString();

        user.setNewEmail(request.newEmail());
        user.setConfirmationToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(30));

        userRepository.save(user);

        // Yangi emailga link yuborish
        String fullName = user.getFirstName() + " " + user.getLastName();
        emailService.sendEmailConfirmation(request.newEmail(), token, fullName);

        log.info("Email o'zgartirish tasdiqlash linki yuborildi: {}", request.newEmail());
    }


    @Override
    public void confirmEmail(String token) {

        log.info("Email tasdiqlash urinishi");

        String cleanToken = token.trim();

        User user = userRepository.findByConfirmationToken(cleanToken)
                .orElseThrow(() -> {
                    log.warn("Email tasdiqlash - token topilmadi");
                    return new RequestException("Token noto'g'ri");
                });

        // Token muddati o'tmaganmi
        if (LocalDateTime.now().isAfter(user.getTokenExpiry())) {
            log.warn("Email tasdiqlash - token muddati o'tgan: {}", user.getEmail());
            throw new RequestException("Token muddati o'tgan");
        }

        String oldEmail = user.getEmail();

        user.setEmail(user.getNewEmail());
        user.setNewEmail(null);
        user.setConfirmationToken(null);
        user.setTokenExpiry(null);

        userRepository.save(user);

        log.info("Email muvaffaqiyatli o'zgartirildi: {} -> {}", oldEmail, user.getEmail());
    }
}