package uz.testplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.testplatform.entity.Question;
import uz.testplatform.enums.TestLevel;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // Test bo'yicha daraja sonini hisoblash (statistika uchun)
    long countByTestIdAndLevel(Long testId, TestLevel level);


    // Test bo'yicha ma'lum darajadagi savollarni variantlari bilan olish
    @Query("""
        SELECT q FROM Question q
        LEFT JOIN FETCH q.variants
        WHERE q.test.id = :testId AND q.level = :level
    """)
    List<Question> findByTestIdAndLevelWithVariants(Long testId, TestLevel level);


    // Bitta savolni variantlari bilan olish
    @Query("""
        SELECT q FROM Question q
        LEFT JOIN FETCH q.variants
        WHERE q.id = :id
    """)
    Optional<Question> findByIdWithVariants(Long id);


    // YANGI - Daraja bo'yicha RANDOM savol tanlash (user test boshlaganda)
    @Query(value = """
        SELECT * FROM questions q
        WHERE q.test_id = :testId AND q.level = :level
        ORDER BY RANDOM()
        LIMIT :limit
    """, nativeQuery = true)
    List<Question> findRandomByTestIdAndLevel(Long testId, String level, int limit);

    @Query("""
        SELECT DISTINCT q FROM Question q
        LEFT JOIN FETCH q.variants
        WHERE q.id IN :ids
    """)
    List<Question> findWithVariants(@org.springframework.data.repository.query.Param("ids") List<Long> ids);
}