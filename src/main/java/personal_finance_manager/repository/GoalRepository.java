package personal_finance_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import personal_finance_manager.entity.Goal;

public interface GoalRepository
        extends JpaRepository<
        Goal,
        Long> {

}