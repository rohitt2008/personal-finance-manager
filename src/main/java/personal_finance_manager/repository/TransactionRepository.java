package personal_finance_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import personal_finance_manager.entity.Transaction;
import personal_finance_manager.entity.User;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserOrderByDateDescIdDesc(User user);

    List<Transaction> findByUserAndDateBetweenOrderByDateDescIdDesc(User user, String startDate, String endDate);

    Optional<Transaction> findByIdAndUser(Long id, User user);

    boolean existsByCategoryAndUser(String category, User user);

    List<Transaction> findByUserAndDateGreaterThanEqual(User user, String startDate);
}