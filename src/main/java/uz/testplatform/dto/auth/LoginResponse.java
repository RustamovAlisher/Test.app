package uz.testplatform.dto.auth;

import uz.testplatform.enums.Role;


public record LoginResponse(
        String token,
        String email,
        Role role
) {
}