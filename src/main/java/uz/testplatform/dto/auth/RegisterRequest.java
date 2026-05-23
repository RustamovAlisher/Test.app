package uz.testplatform.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank(message = "Ism bush bulmasin")

        String firstName,

        @NotBlank(message = "familiya bush bulmasin")
        String lastName,

        @NotBlank(message = "email bush bulmasin")
        @Email(message = "email format xato")
        String email,

        @NotBlank(message = "password bush bo'lmasin")
        String password,

        @NotBlank(message = "passport code bush bo'lmasin")
        String passportCode
) {
}
