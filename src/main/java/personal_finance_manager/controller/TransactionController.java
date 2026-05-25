package personal_finance_manager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import personal_finance_manager.dto.CreateTransactionRequest;
import personal_finance_manager.dto.UpdateTransactionRequest;

import personal_finance_manager.entity.Transaction;
import personal_finance_manager.service.TransactionService;

import java.util.List;

@RestController
@RequestMapping(
        "/api/transactions"
)

@RequiredArgsConstructor

public class TransactionController {


    private final
    TransactionService transactionService;



    @PostMapping

    public String create(

            @RequestBody
            CreateTransactionRequest request

    ) {

        return transactionService
                .create(
                        request
                );

    }



    @GetMapping

    public List<Transaction>
    getAll() {

        return transactionService
                .getAll();

    }



    @DeleteMapping(
            "/{id}"
    )

    public String delete(

            @PathVariable
            Long id

    ) {

        return transactionService
                .delete(
                        id
                );

    }



    @PutMapping(
            "/{id}"
    )

    public String update(

            @PathVariable
            Long id,


            @Valid
            @RequestBody
            UpdateTransactionRequest request

    ) {

        return transactionService
                .update(

                        id,

                        request

                );

    }

}