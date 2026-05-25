package personal_finance_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import personal_finance_manager.entity.Category;
import personal_finance_manager.entity.User;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByNameAndUser(String name, User user);

    Optional<Category> findByNameAndUserIsNull(String name);

    List<Category> findByUserOrUserIsNull(User user);

    boolean existsByNameAndUser(String name, User user);

    boolean existsByNameAndUserIsNull(String name);
}