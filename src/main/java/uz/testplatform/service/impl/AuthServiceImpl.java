package uz.testplatform.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import uz.testplatform.util.JwtUtil;

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
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    @Override
    public UserResponse register(RegisterRequest request) {

        log.info("Ro'yxatdan o'tish boshlandi: {}", request.email());
        if (userRepository.existsByEmail(request.email())) {
            log.warn("Email allaqachon band: {}", request.email());
            throw new ConflictException("Bu email allaqachon ro'yxatdan o'tgan");
        }
        if (userRepository.existsByPassportCode(request.passportCode())) {
            log.warn("Pasport band: {}", request.passportCode());
            throw new ConflictException("Bu pasport kodi allaqachon ro'yxatdan o'tgan");
        }
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        User savedUser = userRepository.save(user);
        log.info("Yangi user yaratildi: id={}, email={}", savedUser.getId(), savedUser.getEmail());
        return userMapper.toResponse(savedUser);
    }


    @Override
    public LoginResponse login(LoginRequest request) {

        log.info("Login urinish: {}", request.email());
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Login xato - email topilmadi: {}", request.email());
                    return new AuthorizationException("Email yoki parol noto'g'ri");
                });
        boolean passwordMatches = passwordEncoder.matches(request.password(), user.getPassword());

        if (!passwordMatches) {
            log.warn("Login xato - parol noto'g'ri: {}", request.email());
            throw new AuthorizationException("Email yoki parol noto'g'ri");
        }
        String token = jwtUtil.generateToken(user);
        LocalDateTime expiresAt = jwtUtil.getExpirationTime();
        log.info("Login muvaffaqiyatli: {}", user.getEmail());
        return new LoginResponse(token, user.getEmail(), user.getRole(), expiresAt);
    }


    @Override
    public void changeEmail(ChangeEmailRequest request, String currentUserEmail) {
        log.info("Email o'zgartirish: {} -> {}", currentUserEmail, request.newEmail());
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new NotFoundException("Foydalanuvchi topilmadi"));
        if (!user.getFirstName().equals(request.firstName())) {
            log.warn("Email o'zgartirish - ism mos kelmadi");
            throw new RequestException("Ma'lumotlar mos kelmadi");
        }
        if (!user.getLastName().equals(request.lastName())) {
            log.warn("Email o'zgartirish - familiya mos kelmadi");
            throw new RequestException("Ma'lumotlar mos kelmadi");
        }
        if (!user.getPassportCode().equals(request.passportCode())) {
            log.warn("Email o'zgartirish - pasport mos kelmadi");
            throw new RequestException("Ma'lumotlar mos kelmadi");
        }
        if (userRepository.existsByEmail(request.newEmail())) {
            log.warn("Yangi email band: {}", request.newEmail());
            throw new ConflictException("Bu email allaqachon band");
        }
        String token = UUID.randomUUID().toString();
        user.setNewEmail(request.newEmail());
        user.setConfirmationToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);
        String fullName = user.getFirstName() + " " + user.getLastName();
        emailService.sendEmailChangeConfirmation(request.newEmail(), token, fullName);
        log.info("Email o'zgartirish linki yuborildi: {}", request.newEmail());
    }


    @Override
    public void confirmEmail(String token) {
        log.info("Email tasdiqlash urinishi");
        String cleanToken = token.trim();
        User user = userRepository.findByConfirmationToken(cleanToken)
                .orElseThrow(() -> {
                    log.warn("Token topilmadi");
                    return new RequestException("Token noto'g'ri");
                });

        if (LocalDateTime.now().isAfter(user.getTokenExpiry())) {
            log.warn("Token muddati o'tgan: {}", user.getEmail());
            throw new RequestException("Token muddati o'tgan");
        }

        String oldEmail = user.getEmail();
        user.setEmail(user.getNewEmail());
        user.setNewEmail(null);
        user.setConfirmationToken(null);
        user.setTokenExpiry(null);
        userRepository.save(user);
        log.info("Email o'zgartirildi: {} -> {}", oldEmail, user.getEmail());
    }
}