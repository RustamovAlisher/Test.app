package uz.testplatform.dto.user;

import uz.testplatform.enums.Role;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String passportCode,
        Role role,
        LocalDateTime createdAt
) {
}