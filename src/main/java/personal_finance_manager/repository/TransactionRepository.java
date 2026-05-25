package personal_finance_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import personal_finance_manager.entity.Transaction;

public interface TransactionRepository
        extends JpaRepository<
        Transaction,
        Long
        > {

}