package personal_finance_manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import personal_finance_manager.dto.CreateTransactionRequest;
import personal_finance_manager.entity.Transaction;
import personal_finance_manager.repository.TransactionRepository;
import personal_finance_manager.dto.UpdateTransactionRequest;

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

    public String update(

            Long id,

            UpdateTransactionRequest request

    ) {

        Transaction transaction =
                transactionRepository

                        .findById(
                                id
                        )

                        .orElseThrow(

                                () -> new RuntimeException(
                                        "Transaction not found"
                                )

                        );



        if (

                request.getAmount()
                        != null

        ) {

            transaction.setAmount(

                    request.getAmount()

            );

        }



        if (

                request.getDescription()
                        != null

        ) {

            transaction.setDescription(

                    request.getDescription()

            );

        }



        if (

                request.getCategory()
                        != null

        ) {

            transaction.setCategory(

                    request.getCategory()

            );

        }



        transactionRepository
                .save(
                        transaction
                );


        return "Transaction updated";

    }

}