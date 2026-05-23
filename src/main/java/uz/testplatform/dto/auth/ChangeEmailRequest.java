package uz.testplatform.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record ChangeEmailRequest(

        @NotBlank(message = "Ism bo'sh bo'lmasin")
        String firstName,

        @NotBlank(message = "Familiya bo'sh bo'lmasin")
        String lastName,

        @NotBlank(message = "Pasport kod bo'sh bo'lmasin")
        String passportCode,

        @NotBlank(message = "Yangi email bo'sh bo'lmasin")
        @Email(message = "Email format noto'g'ri")
        String newEmail
) {
}