package personal_finance_manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import personal_finance_manager.dto.CreateTransactionRequest;
import personal_finance_manager.entity.Transaction;
import personal_finance_manager.repository.TransactionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor

public class TransactionService {

    private final
    TransactionRepository transactionRepository;


    public String create(

            CreateTransactionRequest request

    ) {

        Transaction transaction =
                Transaction.builder()

                        .amount(
                                request.getAmount()
                        )

                        .date(
                                request.getDate()
                        )

                        .description(
                                request.getDescription()
                        )

                        .category(
                                request.getCategory()
                        )

                        .build();

        transactionRepository
                .save(transaction);

        return "Transaction created";

    }



    public List<Transaction>
    getAll() {

        return transactionRepository
                .findAll();

    }
    public String delete(
            Long id
    ) {

        transactionRepository
                .deleteById(id);

        return "Transaction deleted";

    }

}