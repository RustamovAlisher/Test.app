package uz.testplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.testplatform.entity.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

}