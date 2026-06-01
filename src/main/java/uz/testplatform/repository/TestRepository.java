package uz.testplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.testplatform.entity.Test;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    // JpaRepository asosiy metodlari yetadi:
    // - findAll(pageable)  → pagination
    // - findById(id)       → bitta test (savolsiz)
    // - save, deleteById, existsById
}