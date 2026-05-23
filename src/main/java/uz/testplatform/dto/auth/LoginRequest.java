package uz.testplatform.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public record LoginRequest(

        @NotBlank(message = "Email bo'sh bo'lmasin")
        @Email(message = "Email format noto'g'ri")
        String email,

        @NotBlank(message = "Parol bo'sh bo'lmasin")
        String password
) {
}