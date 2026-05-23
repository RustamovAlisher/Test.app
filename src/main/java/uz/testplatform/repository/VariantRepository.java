package uz.testplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.testplatform.entity.Variant;

@Repository
public interface VariantRepository extends JpaRepository<Variant, Long> {


}