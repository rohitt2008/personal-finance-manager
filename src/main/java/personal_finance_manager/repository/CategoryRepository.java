package personal_finance_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import personal_finance_manager.entity.Category;

public interface CategoryRepository
        extends JpaRepository<
        Category,
        Long
        > {

}