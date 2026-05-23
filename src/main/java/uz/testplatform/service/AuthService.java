package uz.testplatform.service;

import uz.testplatform.dto.auth.*;
import uz.testplatform.dto.user.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    void changeEmail(ChangeEmailRequest request, String currentUserEmail);

    void confirmEmail(String token);

}
