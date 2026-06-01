package uz.testplatform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.testplatform.entity.Result;

import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {


    @Query("""
        SELECT r FROM Result r
        LEFT JOIN FETCH r.answers a
        LEFT JOIN FETCH a.question q
        WHERE r.id = :id
    """)
    Optional<Result> findByIdWithAnswers(Long id);

    @Query("""
        SELECT r FROM Result r
        JOIN FETCH r.user
        JOIN FETCH r.test
        WHERE r.id = :id
    """)
    Optional<Result> findByIdWithUserAndTest(Long id);

    @Query(
            value = """
                SELECT r FROM Result r
                JOIN FETCH r.user
                JOIN FETCH r.test
            """,
            countQuery = "SELECT count(r) FROM Result r"
    )
    Page<Result> findAllWithUserAndTest(Pageable pageable);

    @Query(
            value = """
                SELECT r FROM Result r
                JOIN FETCH r.test
                WHERE r.user.id = :userId
            """,
            countQuery = "SELECT count(r) FROM Result r WHERE r.user.id = :userId"
    )
    Page<Result> findByUserIdWithTest(Long userId, Pageable pageable);

    Optional<Result> findByResultCode(String resultCode);

}