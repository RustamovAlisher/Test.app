package uz.testplatform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.testplatform.dto.auth.ChangeEmailRequest;
import uz.testplatform.dto.auth.LoginRequest;
import uz.testplatform.dto.auth.LoginResponse;
import uz.testplatform.dto.auth.RegisterRequest;
import uz.testplatform.dto.user.UserResponse;
import uz.testplatform.service.AuthService;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Ro'yxatdan o'tish va login")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Ro'yxatdan o'tish")
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(summary = "Login - JWT token qaytaradi")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Email o'zgartirish so'rovi (login bo'lgan user)")
    @PostMapping("/change-email")
    public ResponseEntity<String> changeEmail(
            @Valid @RequestBody ChangeEmailRequest request,
            Principal principal) {

        String currentEmail = principal.getName();
        authService.changeEmail(request, currentEmail);

        return ResponseEntity.ok("Tasdiqlash linki yangi emailga yuborildi");
    }

    @Operation(summary = "Email tasdiqlash (link orqali)")
    @GetMapping("/confirm-email")
    public ResponseEntity<String> confirmEmail(@RequestParam String token) {
        authService.confirmEmail(token);
        return ResponseEntity.ok("Email muvaffaqiyatli o'zgartirildi");
    }
}