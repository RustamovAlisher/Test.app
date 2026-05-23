package uz.testplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.testplatform.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByConfirmationToken(String token);

    boolean existsByEmail(String email);

    boolean existsByPassportCode(String passportCode);

}
