package uz.testplatform.dto.auth;

import uz.testplatform.enums.Role;

import java.time.LocalDateTime;

public record LoginResponse(
        String token,
        String email,
        Role role,
        LocalDateTime expiresAt
) {
}