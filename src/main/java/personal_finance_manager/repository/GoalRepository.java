package personal_finance_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import personal_finance_manager.entity.Goal;
import personal_finance_manager.entity.User;

import java.util.List;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findByUser(User user);

    Optional<Goal> findByIdAndUser(Long id, User user);
}