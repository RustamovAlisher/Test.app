package uz.testplatform.mapper;

import org.springframework.stereotype.Component;
import uz.testplatform.dto.auth.RegisterRequest;
import uz.testplatform.dto.user.UserResponse;
import uz.testplatform.entity.User;


@Component
public class UserMapper {


    public User toEntity(RegisterRequest request) {
        return User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(request.password())
                .passportCode(request.passportCode())
                .build();
    }


    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassportCode(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}